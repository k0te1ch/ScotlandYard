package solution.controllers;

import scotlandyard.*;
import solution.Constants;
import solution.Models.GameRecordTracker;
import solution.MrXHistoryTracker;
import solution.ScotlandYardModel;
import solution.helpers.ColourHelper;
import solution.helpers.SetupHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.GameUIInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * This represents the controller in our MVC paradigm. It allows the view to retrieve relevant information about the game state
 * while providing a decoupled environment between the view and controller. To do this we use {@link solution.interfaces.GameControllerInterface}
 * to communicate to the controller from the view and {@link solution.interfaces.GameUIInterface} to communicate updates to
 * the views.
 */
public class GameController implements GameControllerInterface {

    private enum LoadState {LOAD_REPLAY,LOAD_NO_REPLAY,NOT_LOADING}

    private ScotlandYardModel model;
    private Set<GameUIInterface> listeners;
    private MrXHistoryTracker mrXHistoryTracker;
    private UIPlayer uiPlayer;
    private GameRecordTracker gameRecordTracker;
    private LoadState loadState = LoadState.NOT_LOADING;

    public GameController(){
        listeners = new HashSet<GameUIInterface>();
        mrXHistoryTracker = new MrXHistoryTracker();
        gameRecordTracker = new GameRecordTracker();
        uiPlayer = new UIPlayer();
    }


    @Override
    public void saveGame(File fileLocation) {
        try {
            gameRecordTracker.save(fileLocation, model);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void loadGame(File fileLocation, boolean replay) {
        try {
            resetGameData();
            model = gameRecordTracker.load(fileLocation, uiPlayer);

            model.spectate(mrXHistoryTracker);

            loadState = replay ? LoadState.LOAD_REPLAY : LoadState.LOAD_NO_REPLAY;

            notifyListenersModelUpdated();

            if(!replay) {
                tryNextTrackerMove();
            }

            for(GameUIInterface uiInterface : listeners){
                uiInterface.showGameInterface();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isMrXVisible() {
        return model.getRealPlayerLocation(Constants.MR_X_COLOUR) == model.getPlayerLocation(Colour.Black);
    }

    private void resetGameData() {
        mrXHistoryTracker = new MrXHistoryTracker();
        gameRecordTracker = new GameRecordTracker();
        uiPlayer = new UIPlayer();
        loadState = LoadState.NOT_LOADING;
    }

    @Override
    public int getPlayerVisiblePosition(Colour colour) {
        return model.getPlayerLocation(colour);
    }

    public void addUpdateListener(GameUIInterface listener){
        listeners.add(listener);
    }

    public void removeUpdateListener(GameUIInterface listener){
        listeners.remove(listener);
    }

    /**
     * Creates and starts a new {@link solution.ScotlandYardModel}
     *
     * @param playerCount the number of players to create the {@link solution.ScotlandYardModel} with
     */
    private void startNewGame(final int playerCount) {
        try {
            resetGameData();
            model = new ScotlandYardModel(playerCount-1, getRounds(), "graph.txt");



            ArrayList<Integer> playerLocations = new ArrayList<Integer>();
            int maxNodeNum = model.getGraph().getNodes().size();
            Random random = new Random();

            //adds each player while making sure no two players start on the same location
            for (int i = 0; i < playerCount; i++) {
                final Colour colour = ColourHelper.getColour(i);

                int location = random.nextInt(maxNodeNum);
                while(playerLocations.contains(location)){
                    location = random.nextInt(maxNodeNum);
                }
                playerLocations.add(location);

                model.join(uiPlayer, colour, location, SetupHelper.getTickets(colour.equals(Constants.MR_X_COLOUR)));
            }


            //begin tracking the game
            model.spectate(mrXHistoryTracker);
            gameRecordTracker.track(model);

        } catch (IOException e) {
            e.printStackTrace();
        }
        notifyListenersModelUpdated();
    }

    private void notifyListenersModelUpdated() {
        for(GameUIInterface gameInterface : listeners) {
            gameInterface.onGameModelUpdated(this);
        }
    }

    @Override
    public boolean isGameOver(){
        return model.isGameOver();
    }

    @Override
    public Set<Colour> getWinningPlayers() {
        return model.getWinningPlayers();
    }

    @Override
    public List<MoveTicket> getMrXHistory() {
        return mrXHistoryTracker.getMoveHistory();
    }

    @Override
    public Colour getCurrentPlayer(){
        return model.getCurrentPlayer();
    }

    @Override
    public Map<Ticket, Integer> getPlayerTickets(Colour currentPlayer){
        if(currentPlayer == Constants.MR_X_COLOUR){
            System.out.println();
        }

        return model.getAllPlayerTickets(currentPlayer);
    }

    @Override
    public List<Colour> getPlayerList() {
        return model.getPlayers();
    }

    @Override
    public List<MoveTicket> getValidSingleMovesAtLocation(Colour player, int location) {
        return model.getAvailableSingleMoves(model.getGraph(), player, location, model.getAllPlayerTickets(player));
    }

    @Override
    public List<MoveTicket> getValidSecondMovesAtLocation(Colour player, int location, Ticket firstTicket) {
        Map<Ticket, Integer> playerTickets = new HashMap<Ticket, Integer>(model.getAllPlayerTickets(player));
        playerTickets.put(firstTicket, playerTickets.get(firstTicket)-1);
        return model.getAvailableSingleMoves(model.getGraph(), player, location, playerTickets);
    }

    @Override
    public int getCurrentPlayerRealPosition() {
        return model.getRealPlayerLocation(model.getCurrentPlayer());
    }

    @Override
    public void notifyAllPlayersAdded(int count) {
        startNewGame(count);

        for(GameUIInterface uiInterface : listeners){
            uiInterface.showGameInterface();
        }

    }

    @Override
    public void notifyMapLoaded() {
        if(loadState == LoadState.LOAD_REPLAY) {
            tryNextTrackerMove();
        }
    }

    private void tryNextTrackerMove() {
        Move currentMove = gameRecordTracker.getCurrentMove();
        if(currentMove != null){
                if(loadState == LoadState.LOAD_REPLAY){
                    notifyMoveSelected(currentMove);
                }else{
                    uiPlayer.setPendingMove(currentMove);
                    model.turn();
                    gameRecordTracker.nextMove();
                    tryNextTrackerMove();
                }
        }else{
            loadState = LoadState.NOT_LOADING;
        }
    }

    @Override
    public void notifyMoveSelected(Move move) {
        uiPlayer.setPendingMove(move);

        if(move instanceof MovePass){
            notifyMoveAnimationFinished();
        }else {
            notifyListenersAnimateMove(move);
        }

    }

    private void notifyListenersAnimateMove(Move move) {
        MoveTicket firstMove = null;
        MoveTicket secondMove = null;

        if(move instanceof MoveTicket){
            firstMove = (MoveTicket) move;
        }else if(move instanceof MoveDouble){
            firstMove = (MoveTicket) ((MoveDouble)move).moves.get(0);
            secondMove = (MoveTicket) ((MoveDouble)move).moves.get(1);
        }

        for(GameUIInterface gameUIInterface : listeners){
            gameUIInterface.animateMove(firstMove, secondMove);
        }
    }

    @Override
    public void notifyMoveAnimationFinished() {

        model.turn();


            notifyListenersModelUpdated();

            // Is the game over?
            if(isGameOver()){
                System.out.printf("The game is over ):");
            }

        if(gameRecordTracker.getCurrentMove() != null && loadState == LoadState.LOAD_REPLAY){
            gameRecordTracker.nextMove();
            tryNextTrackerMove();
        }
    }

    public List<Colour> getPlayers(){
        return model.getPlayers();
    }
    public static List<Boolean> getRounds() {
        List<Boolean> rounds = new ArrayList<Boolean>();
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(true);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        rounds.add(false);
        return rounds;
    }





}
