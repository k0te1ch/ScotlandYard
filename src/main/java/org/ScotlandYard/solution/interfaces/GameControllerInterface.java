package solution.interfaces;

import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is used to communicate from views to the controller
 */
public interface GameControllerInterface {

    /**
     * Adds a {@link solution.interfaces.GameUIInterface} to the
     * implementation of {@link solution.interfaces.GameControllerInterface}
     *
     * @param listener the listener to be added
     */
    public void addUpdateListener(GameUIInterface listener);

    /**
     * removes a {@link solution.interfaces.GameUIInterface} from the
     * implementation of {@link solution.interfaces.GameControllerInterface}
     *
     * @param listener the listener to be removed
     */
    public void removeUpdateListener(GameUIInterface listener);

    /**
     * The following are a list of convenience data methods for the views
     */

    /**
     *
     * @return a list of all players in the current game
     */
    public List<Colour> getPlayerList();

    /**
     *
     * @return the player whose turn it currently is
     */
    public Colour getCurrentPlayer();

    /**
     * This method is used to get the current player's real position.
     * It's useful when displaying Mr X's real location on his turn.
     *
     * @return the current player's real position
     */
    public int getCurrentPlayerRealPosition();

    /**
     * This method gets the specified player's visible position,
     * that is, their last known position.
     *
     * @param colour the player whose position is required
     * @return the player's last known position
     */
    public int getPlayerVisiblePosition(Colour colour);

    /**
     *
     * @param currentPlayer the player whose tickets are required
     * @return a list of the player's tickets
     */
    public Map<Ticket,Integer> getPlayerTickets(Colour currentPlayer);

    /**
     * This method gets the list of valid moves the specified player may
     * perform from their location
     *
     * @param player the player whose moves are required
     * @param location the player's current location
     * @return a list of possible moves for the player from the specified location
     */
    public List<MoveTicket> getValidSingleMovesAtLocation(Colour player, int location);

    /**
     * This method gets the list of valid moves the specified player may
     * perform from their location after a first move has been performed.
     * The move type is specified by firstTicket and taken into account when
     * calculating the valid moves.
     *
     * @param player the player whose moves are required
     * @param location the player's current location
     * @param firstTicket the {@link scotlandyard.Ticket} used in the first move.
     * @return a list of possible moves for the player from the specified location
     */
    public List<MoveTicket> getValidSecondMovesAtLocation(Colour player, int location, Ticket firstTicket);

    /**
     *
     * @return a list of Mr X's moves
     */
    public List<MoveTicket> getMrXHistory();

    /**
     * This method will return the list of winning players,
     * or an empty list if there are currently none.
     *
     * @return the set of winning players if there are
     */
    public Set<Colour> getWinningPlayers();

    /**
     *
     * @return true if the game is over
     */
    public boolean isGameOver();

    /**
     * This method loads a game from file
     *
     * @param fileLocation the location of the saved file
     * @param replay whether or not the game should be animated back move by move
     */
    public void loadGame(File fileLocation, boolean replay);

    /**
     *
     * @return true if Mr X is currently visible
     */
    public boolean isMrXVisible();


    /**
     * This method saves the current game to file
     *
     * @param fileLocation the lactation to save the file to
     */
    public void saveGame(File fileLocation);



    /**
     * The following are a list of notify methods which allow view components
     * to inform the implementation of {@link solution.interfaces.GameControllerInterface} of changes
     */




    /**
     * This notifies the interface implementation that all players have been added to the
     * game
     * @param count the number of players that have been added
     */
    public void notifyAllPlayersAdded(final int count);

    /**
     * This notifies the interface implementation that the Game Map has been loaded
     * and hence further actions relying on load can begin.
     */
    public void notifyMapLoaded();

    /**
     * This notifies the interface implementation that the current move animation has
     * finished.
     */
    public void notifyMoveAnimationFinished();

    /**
     * This notifies the interface implementation that a move has been selected
     *
     * @param move the selected move
     */
    public void notifyMoveSelected(final Move move);





}
