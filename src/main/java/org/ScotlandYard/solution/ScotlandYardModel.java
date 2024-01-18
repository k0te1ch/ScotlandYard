package solution;

import scotlandyard.*;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class ScotlandYardModel extends ScotlandYard {


    private final List<Boolean> mRounds;
    private final Graph<Integer, Route> mGraph;
    private final LinkedHashMap<Colour, PlayerHolder> mPlayerMap;
    private final List<Spectator> mSpectators;
    private final ArrayList<Colour> colourList;
    private final int mNumberOfDetectives;
    private final String mGraphName;

    private Colour mCurrentPlayerColour = Constants.MR_X_COLOUR;
    private int mCurrentRound = 0;

    public ScotlandYardModel(int numberOfDetectives, List<Boolean> rounds, String graphFileName) throws IOException {
        super(numberOfDetectives, rounds, graphFileName);
        mGraphName = graphFileName;
        mRounds = rounds;
        final URL resource = getClass().getClassLoader().getResource(graphFileName);
        final String filename = URLDecoder.decode(resource.getFile(), "UTF-8");
        mGraph = new ScotlandYardGraphReader().readGraph(filename);
        mNumberOfDetectives = numberOfDetectives;
        mPlayerMap = new LinkedHashMap<Colour, PlayerHolder>();
        mSpectators = new ArrayList<Spectator>();
        colourList = new ArrayList<Colour>();
    }

    @Override
    protected Move getPlayerMove(Colour colour) {
        return mPlayerMap.get(colour).getMove(validMoves(colour));
    }

    @Override
    protected void nextPlayer() {
        final int pos = colourList.indexOf(mCurrentPlayerColour);
        mCurrentPlayerColour = (pos + 1) < mPlayerMap.size() ? colourList.get(pos + 1) : colourList.get(0);
    }

    @Override
    protected void play(MoveTicket move) {
        mPlayerMap.get(move.colour).setCurrentLocation(move.target);

        // Decrease the ticket that has been used by the player
        Map<Ticket, Integer> playerTickets = mPlayerMap.get(move.colour).getTickets();
        playerTickets.put(move.ticket, playerTickets.get(move.ticket) - 1);

        if (move.colour == Constants.MR_X_COLOUR) {
            if(mRounds.get(mCurrentRound)) {
                mPlayerMap.get(move.colour).updateVisiblePosition();
            }
            mCurrentRound++;
        } else {
            // Add the ticket to MrX's stash if it wasn't Mr X who played
            Map<Ticket, Integer> mrXTickets = mPlayerMap.get(Constants.MR_X_COLOUR).getTickets();
            mrXTickets.put(move.ticket, mrXTickets.get(move.ticket) + 1);
        }

        if (move.colour == Constants.MR_X_COLOUR && !mRounds.get(mCurrentRound)) {
            move = new MoveTicket(Constants.MR_X_COLOUR, getPlayerLocation(Constants.MR_X_COLOUR), move.ticket);
        }
        notifySpectators(move);
    }

    @Override
    protected void play(MoveDouble move) {
        notifySpectators(move);

        Map<Ticket, Integer> tickets = mPlayerMap.get(move.colour).getTickets();
        tickets.put(Ticket.DoubleMove, tickets.get(Ticket.DoubleMove) - 1);

        for (Move innerMove : move.moves) {
            play(innerMove);
        }
    }

    @Override
    protected void play(MovePass move) {
        notifySpectators(move);
        //we do nothing right now
    }

    private void notifySpectators(final Move move) {
        for (Spectator spectator : mSpectators) {
            spectator.notify(move);
        }
    }

    public List<MoveTicket> getAvailableSingleMoves(Graph<Integer, Route> mGraph, Colour playerColour, int location, Map<Ticket, Integer> tickets) {

        List<MoveTicket> moves = new ArrayList<MoveTicket>();

        for (Edge<Integer, Route> edge : GraphHelper.getConnectedEdges(mGraph, new Node<Integer>(location))) {

            Integer firstNodePos = null;
            if (edge.source() == location) {
                firstNodePos = edge.target();
            } else if (edge.target() == location) {
                firstNodePos = edge.source();
            }

            Ticket requiredTicket = Ticket.fromRoute(edge.data());
            if (!detectiveOnNode(firstNodePos) && tickets.containsKey(requiredTicket) && tickets.get(requiredTicket) > 0) {
                moves.add(new MoveTicket(playerColour, firstNodePos, requiredTicket));
            }

            if (!detectiveOnNode(firstNodePos) && tickets.containsKey(Ticket.SecretMove) && tickets.get(Ticket.SecretMove) > 0) {
                moves.add(new MoveTicket(playerColour, firstNodePos, Ticket.SecretMove));
            }


        }

        return moves;
    }

    @Override
    public List<Move> validMoves(Colour player) {
        final PlayerHolder playerHolder = mPlayerMap.get(player);

        int playerPos = playerHolder.getRealPosition();

        List<Move> validMoves = new ArrayList<Move>();

        List<MoveTicket> firstMoves = getAvailableSingleMoves(mGraph, player, playerPos, playerHolder.getTickets());

        validMoves.addAll(firstMoves);

        if (player == Constants.MR_X_COLOUR && playerHolder.hasEnoughTickets(Ticket.DoubleMove)) {
            for (MoveTicket firstMove : firstMoves) {

                //remove the ticket we used in the first turn
                Map<Ticket, Integer> secondaryTickets = new HashMap<Ticket, Integer>(playerHolder.getTickets());
                secondaryTickets.put(firstMove.ticket, secondaryTickets.get(firstMove.ticket) - 1);

                List<MoveTicket> secondMoves = getAvailableSingleMoves(mGraph, player, firstMove.target, secondaryTickets);

                for (MoveTicket secondMove : secondMoves) {
                    validMoves.add(new MoveDouble(player, firstMove, secondMove));
                }

            }
        }

        // If no possible moves, then return a pass
        if (validMoves.size() == 0 && player != Constants.MR_X_COLOUR) {
            validMoves.add(new MovePass(player));
        }

        return validMoves;
    }

    private boolean detectiveOnNode(Integer node) {
        for (Colour colour : mPlayerMap.keySet()) {
            PlayerHolder playerHolder = mPlayerMap.get(colour);
            if (playerHolder.getColour() != Constants.MR_X_COLOUR && playerHolder.getVisiblePosition() == node) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void spectate(Spectator spectator) {
        mSpectators.add(spectator);
    }

    @Override
    public boolean join(Player player, Colour colour, int location, Map<Ticket, Integer> tickets) {
        if(!mPlayerMap.containsKey(colour)) {
            mPlayerMap.put(colour, new PlayerHolder(player, colour, location, tickets));
            // Add the current player to the colour list
            colourList.add(colour);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<Colour> getPlayers() {
        List<Colour> colourList = new ArrayList<Colour>();
        colourList.addAll(mPlayerMap.keySet());
        return colourList;
    }

    @Override
    public Set<Colour> getWinningPlayers() {
        Set<Colour> winningPlayers = new HashSet<Colour>();
        if (isGameOver()) {
            if (isMrXWinner()) {
                winningPlayers.add(Constants.MR_X_COLOUR);
            } else {
                winningPlayers.addAll(mPlayerMap.keySet());
                winningPlayers.remove(Constants.MR_X_COLOUR);
            }
        }
        return winningPlayers;
    }

    public int getRealPlayerLocation(Colour colour){
        return mPlayerMap.get(colour).getRealPosition();
    }

    @Override
    public int getPlayerLocation(Colour colour) {

        PlayerHolder playerHolder = mPlayerMap.get(colour);

        if (playerHolder != null) {
            if (colour != Constants.MR_X_COLOUR || mRounds.get(mCurrentRound)) {
                playerHolder.updateVisiblePosition();
            }
            return playerHolder.getVisiblePosition();
        } else {
            return -1;
        }
    }

    // Get Map of Player Holders
    public Map<Colour, PlayerHolder> getPlayerHolders() {
        return mPlayerMap;
    }

    public void setPlayerName(Colour key, String name) {
        PlayerHolder oldPlayer = mPlayerMap.get(key);
        PlayerHolder newPlayer = new PlayerHolder(oldPlayer.getPlayer(), key, oldPlayer.getRealPosition(), oldPlayer.getTickets());
        newPlayer.setName(name);
        // Replace
        mPlayerMap.remove(key);
        mPlayerMap.put(key, newPlayer);

    }

    @Override
    public int getPlayerTickets(Colour colour, Ticket ticket) {
        PlayerHolder playerHolder = mPlayerMap.get(colour);

        if (playerHolder != null) {
            return playerHolder.getTickets().get(ticket);
        } else {
            return -1;
        }
    }

    public Map<Ticket, Integer> getAllPlayerTickets(Colour colour) {
        PlayerHolder playerHolder = mPlayerMap.get(colour);
        if (playerHolder != null) {
            return playerHolder.getTickets();
        } else {
            System.out.printf("Could not find player");
            return null;
        }
    }

    private boolean areAllDetectivesStuck() {
        for (Colour currentColour : mPlayerMap.keySet()) {

            if (currentColour != Constants.MR_X_COLOUR) {
                if (canMove(currentColour)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canMove(Colour colour) {
        List<Move> moves = validMoves(colour);
        return moves.size() > 0 && !(moves.get(0) instanceof MovePass);
    }

    @Override
    public boolean isGameOver() {
        return isReady() && (areAllTurnsCompleted() || !canMove(Constants.MR_X_COLOUR) || areAllDetectivesStuck() || isDetectiveOnMrX());
    }

    private boolean isDetectiveOnMrX() {
        int mrXPos = mPlayerMap.get(Constants.MR_X_COLOUR).getRealPosition();
        for (Colour currentColour : mPlayerMap.keySet()) {

            if (currentColour != Constants.MR_X_COLOUR) {
                if (getPlayerLocation(currentColour) == mrXPos) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areAllTurnsCompleted() {
        return mCurrentRound >= (mRounds.size() - 1) && mCurrentPlayerColour == Constants.MR_X_COLOUR;
    }

    private boolean isMrXWinner() {
        return (areAllTurnsCompleted() || areAllDetectivesStuck()) && !isDetectiveOnMrX();
    }

    @Override
    public boolean isReady() {
        return mPlayerMap.size() == mNumberOfDetectives + 1;
    }

    @Override
    public Colour getCurrentPlayer() {
        return mCurrentPlayerColour;
    }

    @Override
    public int getRound() {
        return mCurrentRound;
    }

    @Override
    public List<Boolean> getRounds() {
        return mRounds;
    }

    public Graph<Integer, Route> getGraph() {
        return mGraph;
    }

    public String getGraphName() {
        return mGraphName;
    }
}
