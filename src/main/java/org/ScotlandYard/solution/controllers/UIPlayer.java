package solution.controllers;

import scotlandyard.Move;
import scotlandyard.Player;

import java.util.List;

/**
 * This implementation of Player allows us to feed the model moves
 */
public class UIPlayer implements Player {
    private Move mPendingMove;

    public void setPendingMove(Move pendingMove) {
        this.mPendingMove = pendingMove;
    }


    @Override
    public Move notify(int location, List<Move> list) {

            for (Move move : list) {
                if (move.equals(mPendingMove)) {
                    return move;
                }
            }
            return null;
    }
}
