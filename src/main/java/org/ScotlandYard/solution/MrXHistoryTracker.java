package solution;

import scotlandyard.Move;
import scotlandyard.MoveTicket;
import scotlandyard.Spectator;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks Mr X and allows retrieving of his move history
 */
public class MrXHistoryTracker implements Spectator {

    List<MoveTicket> moveHistory;

    public MrXHistoryTracker () {
        moveHistory = new ArrayList<MoveTicket>();
    }

    @Override
    public void notify(Move move) {
        if(move.colour == Constants.MR_X_COLOUR){
            if(move instanceof MoveTicket){
                moveHistory.add((MoveTicket) move);
            }
        }
    }

    public List<MoveTicket> getMoveHistory() {
        return moveHistory;
    }

}