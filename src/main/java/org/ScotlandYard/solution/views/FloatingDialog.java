package solution.views;

import scotlandyard.Colour;
import solution.Constants;
import solution.helpers.ColourHelper;
import solution.interfaces.GameControllerInterface;

import java.awt.*;
import java.util.Set;

/**
 * Created by benallen on 12/03/15.
 */
public class FloatingDialog {
    private int mWidth = 100;
    private int mHeight = 100;
    private String mTitle = "";
    private String mSummary = "";

    public FloatingDialog(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public static FloatingDialog getMovePassInstance(Colour playerColour, int width, int height) {

        FloatingDialog dialog = new FloatingDialog(width, height);

        dialog.setTitle("No possible moves");
        dialog.setSummary(ColourHelper.toString(playerColour) + " will pass their move");

        return dialog;
    }

    public static FloatingDialog getGameoverInstance(GameControllerInterface controllerInterface, int width, int height) {

        FloatingDialog dialog = new FloatingDialog(width, height);


        // Get the winning players and make a label
        Set<Colour> winningPlayers = controllerInterface.getWinningPlayers();
        dialog.setTitle("End of Game");
        dialog.setSummary("No-one has won yet");

        // Decide on the text content
        if(winningPlayers.contains(Constants.MR_X_COLOUR)) {
            dialog.setSummary("Mr X Wins!");
        } else if(winningPlayers.size() > 0) {
            dialog.setSummary("The Detectives win!");
        }

        return dialog;
    }

    private void setTitle(String text) {
        this.mTitle = text;
    }

    private void setSummary(String text) {
        this.mSummary = text;
    }
    public void draw(Graphics2D g2d){

        // Draw Background
        g2d.setColor(new Color(0,0,0,0.7f));
        final int width = (int) (2*(mWidth / 3f));
        final int height = (int) (2*(mHeight / 3f));
        g2d.fillRect((int) (mWidth/2f - width/2f), (int) (mHeight/2f - height/2f), width, height);

        // Draw Text
        g2d.setFont(g2d.getFont().deriveFont(40.0f));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(mSummary);
        int w2 = fm.stringWidth(mTitle);
        g2d.setColor(Color.WHITE);
        g2d.drawString(mTitle, (mWidth / 2) - (w2 / 2), (mHeight / 2) - 50);
        g2d.drawString(mSummary, (mWidth / 2) - (w / 2), (mHeight / 2));

    }
}
