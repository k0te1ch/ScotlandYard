package solution.views;

import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.Constants;
import solution.helpers.ColourHelper;
import solution.helpers.ColourTintHelper;
import solution.helpers.TicketHelper;
import solution.interfaces.GameControllerInterface;
import solution.interfaces.adapters.GameUIAdapter;
import solution.interfaces.adapters.ScrollAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by benallen on 17/03/15.
 */
public class SideBarView extends JPanel {
    private BufferedImage colourImg;
    private GameControllerInterface mControllerInterface;
    private BufferedImage mRoundHolderImg;
    private BufferedImage mCurrentPlayerImg;
    private Font mFont;
    private final int SIDEBAR_WIDTH = 216;
    private final int SIDEBAR_HEIGHT = 800;
    HashMap<Ticket, BufferedImage> ticketToImg;
    private int mAlternator = 0;
    private int mTempYPosTicket = 0;
    private final int mLimitYForTicketStack = 95;
    private final int mTicketStackIncrement = 4;
    private final int mTicketStackIncrLimit = 150;

    SideBarView(GameControllerInterface controllerInterface){
        mControllerInterface = controllerInterface;

        controllerInterface.addUpdateListener(new GameAdapter());

        // Set up sizing
        setSizing();

        // All tickets
        ticketToImg = new HashMap<Ticket, BufferedImage>(5);

        // Load in the images
        loadInImages();
            
        // Load up an image for each ticket
        Ticket[] ticketTypes = {Ticket.Bus, Ticket.Underground, Ticket.Taxi, Ticket.DoubleMove, Ticket.SecretMove};
        for (int i = 0; i < 5; i++){
            ticketToImg.put(ticketTypes[i], TicketHelper.ticketBuffImg(ticketTypes[i]));
        }

        // Grab the font in
        loadInFont();

        // Styling
        setOpaque(false);

        // Add the listeners
        addMouseListener(new LocalMouseAdapter());
        addMouseMotionListener(new LocalMouseAdapter());
        addMouseWheelListener(new LocalScrollAdapter());

    }

    private void setSizing() {
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, SIDEBAR_HEIGHT));
        setMaximumSize(new Dimension(SIDEBAR_WIDTH,SIDEBAR_HEIGHT));
        setMinimumSize(new Dimension(SIDEBAR_WIDTH,SIDEBAR_HEIGHT));
        setSize(new Dimension(SIDEBAR_WIDTH,SIDEBAR_HEIGHT));
    }

    private void loadInFont() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("ui" + File.separator + "snellroundhand.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFont = font;
    }

    private void loadInImages() {
        URL resource1 = getClass().getClassLoader().getResource("ui" + File.separator + "roundholder.png");
        URL resource2 = getClass().getClassLoader().getResource("ui" + File.separator + "currentPlayer.png");
        URL resource3 = getClass().getClassLoader().getResource("ui" + File.separator + "paper.png");
        try {
            mRoundHolderImg = ImageIO.read(new File(resource1.toURI()));
            mCurrentPlayerImg = ImageIO.read(new File(resource2.toURI()));
            colourImg = ImageIO.read(new File(resource3.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Init the graphics interface
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2d.drawImage(mRoundHolderImg, null, 0, 0);
        g2d.drawImage(mCurrentPlayerImg, null, -10, 190);

        // Draw round number
        g2d.setFont(mFont.deriveFont(80f));
        g2d.setColor(Color.BLACK);
        int roundNumber;
        roundNumber = mControllerInterface.getMrXHistory().size();
        roundNumber++;
        if(Constants.MR_X_COLOUR != mControllerInterface.getCurrentPlayer()){
            roundNumber--;
        }
        String roundText = String.valueOf(roundNumber);
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(roundText);
        int tOffset = 160;
        g2d.drawString(roundText, (SIDEBAR_WIDTH / 2) - (w / 2) - 20, tOffset);

        // Draw Current Player iff it is black the change to mr x
        g2d.setFont(mFont.deriveFont(30f));
        String currentPlayerName = mControllerInterface.getCurrentPlayer().toString();
        if(currentPlayerName == "Black"){
            currentPlayerName = "Mr X";
        }
        fm = g.getFontMetrics();
        w = fm.stringWidth(currentPlayerName);
        tOffset = 250;
        g2d.drawString(currentPlayerName, (SIDEBAR_WIDTH / 2) - (w / 2), tOffset);

        // Draw the current players paper
        // Grab the colour and tint the image
        BufferedImage effectedImage;
        Color c = ColourHelper.toColor(mControllerInterface.getCurrentPlayer());
        if(mControllerInterface.getCurrentPlayer() == Constants.MR_X_COLOUR) {
            effectedImage = ColourTintHelper.setBlack(colourImg);
        } else {
            effectedImage = ColourTintHelper.setRGB(colourImg, c);
        }

        g2d.drawImage(
                effectedImage,
                (SIDEBAR_WIDTH / 2) + 20,
                tOffset- 90,
                90,
                90,
                this);

        List<MoveTicket> mrXHistory = mControllerInterface.getMrXHistory();

        // Draw ticket flicker
        // TODO: sort bug for bottom stack full and multiple scrolls down

        TicketFlicker ticketFlicker = new TicketFlicker();
        ticketFlicker.draw(
                mrXHistory,
                mAlternator,
                ticketToImg,
                SIDEBAR_WIDTH,
                mTempYPosTicket,
                g2d);


    }

    class LocalMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
        }
    }

    class GameAdapter extends GameUIAdapter {
        @Override
        public void onGameModelUpdated(GameControllerInterface controllerInterface) {
            repaint();
        }
    }
     class LocalScrollAdapter extends ScrollAdapter {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            
            // If the scroll is upwards then add onto the y value and repaint if inside the limits
            if(e.getPreciseWheelRotation() > 0) {
                if(mTempYPosTicket >= mTicketStackIncrLimit){
                    // Limit the temp y pos to this
                    mTempYPosTicket = mLimitYForTicketStack;
                } else if(mTempYPosTicket < mLimitYForTicketStack){
                    mTempYPosTicket = mTempYPosTicket + mTicketStackIncrement;
                    repaint();
                } else if (mTempYPosTicket >= mLimitYForTicketStack) {
                    mTempYPosTicket = 0;
                    mAlternator++;
                    repaint();
                }

            } else {
                // Decrease the yvalue and repaint if inside the limits
                if(mTempYPosTicket <= -mTicketStackIncrLimit) {
                    // limit the y pos
                    mTempYPosTicket = -mLimitYForTicketStack;
                } else if(mTempYPosTicket >= -mLimitYForTicketStack){
                    mTempYPosTicket = mTempYPosTicket - mTicketStackIncrement;
                    repaint();
                } else if(mTempYPosTicket < -mLimitYForTicketStack) {
                    mTempYPosTicket = 0;
                    mAlternator--;
                    repaint();
                }
            }
        }
    }
}
