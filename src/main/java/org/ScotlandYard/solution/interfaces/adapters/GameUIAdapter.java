package solution.interfaces.adapters;

import scotlandyard.MoveTicket;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.GameUIInterface;

/**
 * An implementation of {@link solution.interfaces.GameUIInterface} which can be extended to override
 * desired methods
 */
public abstract class GameUIAdapter implements GameUIInterface {
    @Override
    public void showGameInterface() {

    }

    @Override
    public void onGameModelUpdated(GameControllerInterface controllerInterface) {

    }

    @Override
    public void animateMove(MoveTicket firstMove, MoveTicket secondMove) {

    }
}
