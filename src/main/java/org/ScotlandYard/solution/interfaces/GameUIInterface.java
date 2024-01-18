package solution.interfaces;

import scotlandyard.MoveTicket;

/**
 * This is used to communicate from the controller to views
 */
public interface GameUIInterface {

    /**
     * This requests the view shows the game ui
     */
    public void showGameInterface();

    /**
     * This notifies the {@link solution.interfaces.GameUIInterface} that changes have occurred
     * to the underlying {@link solution.ScotlandYardModel}
     * @param controllerInterface
     */
    public void onGameModelUpdated(GameControllerInterface controllerInterface);

    /**
     * This requests the ui animate the current player through the moves specified. It is expected
     * that {@link solution.interfaces.GameControllerInterface#notifyMoveAnimationFinished()} will be called when the animation finishes
     *
     * @param firstMove the first move to animate
     * @param secondMove the second move to animate
     */
    public void animateMove(MoveTicket firstMove, MoveTicket secondMove);
}
