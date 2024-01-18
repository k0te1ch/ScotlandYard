package solution.views;

import scotlandyard.Colour;
import scotlandyard.Ticket;
import solution.Constants;
import solution.helpers.ColourHelper;
import solution.helpers.ColourTintHelper;
import solution.interfaces.GameControllerInterface;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * Created by benallen on 16/03/15.
 */
public class PlayerInfoImg {
    private GameControllerInterface mControllerInterface;
    private BufferedImage bgImg;
    private BufferedImage colourImg;
    private Colour mCurrentPlayer;
    private int mPlayerNumber;
    private Font mFont;
    private float xScale = 1.1f;
    private float yScale = 1.1f;
    private ImageObserver mImageObserver;
    public PlayerInfoImg(Colour currentPlayer, GameControllerInterface controllerInterface, int playerNumber){
        // Set the controllers and parameters
        mControllerInterface = controllerInterface;
        mPlayerNumber = playerNumber;
        mCurrentPlayer = currentPlayer;

        // Load in Images
        loadInImages(currentPlayer, playerNumber);

        // Load in font
        loadInFont();

        // Set the observer
        mImageObserver = new ImageObserver() {
            @Override
            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };
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

    private void loadInImages(Colour currentPlayer, int playerNumber) {
        URL resource;
        if (currentPlayer == Constants.MR_X_COLOUR) {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "mrxholder.png");
        } else if(playerNumber % 2 == 0) {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderA.png");
        } else {
            resource = getClass().getClassLoader().getResource("ui" + File.separator + "playerholderB.png");
        }
        URL resource2 = getClass().getClassLoader().getResource("ui" + File.separator + "paper.png");
        try {
            bgImg = ImageIO.read(new File(resource.toURI()));
            colourImg = ImageIO.read(new File(resource2.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g, int xAcross) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        // Draw background image
        g2d.drawImage(
                bgImg,
                xAcross,
                0,
                (bgImg.getWidth() / 2),
                (bgImg.getHeight() / 2),
                mImageObserver);

        // Set up the players name string
        g2d.setFont(mFont.deriveFont(22f));
        g2d.setColor(Color.BLACK);
        String playerName;
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            playerName = "";
        } else {
            playerName = "Player " + (mPlayerNumber);
        }

        // Setup font metrics
        FontMetrics fm = g.getFontMetrics();

        // Temp variable for storing current text
        String currentDrawText;

        // Add in the player name
        drawPlayerName(xAcross, g2d, playerName);

        // Get the tickets
        Map<Ticket, Integer> playerTickets = mControllerInterface.getPlayerTickets(mCurrentPlayer);

        // Setup font size
        if(mCurrentPlayer == Constants.MR_X_COLOUR){
            g2d.setFont(mFont.deriveFont(20f));
        } else {
            g2d.setFont(mFont.deriveFont(28f));
        }


        // Draw Bus Number
        currentDrawText = String.valueOf(playerTickets.get(Ticket.Bus));
        drawBusNumber(
                xAcross,
                g2d,
                fm,
                currentDrawText
        );

        // Add in the taxi number
        currentDrawText = String.valueOf(playerTickets.get(Ticket.Taxi));
        drawTaxiNumber(
                xAcross,
                g2d,
                currentDrawText
        );

        // Add in the underground number
        currentDrawText = String.valueOf(playerTickets.get(Ticket.Underground));
        drawUndergroundNumber(
                xAcross,
                g2d,
                currentDrawText
        );

        // Mr X & Player Extras
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            drawMrXExtra(xAcross, g2d, playerTickets);
        } else {
            drawPaper(xAcross, g2d);
        }

    }

    private void drawPaper(int xAcross, Graphics2D g2d) {
        // Grab the colour and tint the image
        Color c = ColourHelper.toColor(mCurrentPlayer);
        colourImg = ColourTintHelper.setRGB(colourImg, c);
        g2d.drawImage(
                colourImg,
                xAcross + 70,
                50,
                70,
                70,
                new ImageObserver() {
                    @Override
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
    }

    private void drawMrXExtra(int xAcross, Graphics2D g2d, Map<Ticket, Integer> playerTickets) {
        String currentDrawText;
        int lOffset;
        int tOffset;
        currentDrawText = String.valueOf(playerTickets.get(Ticket.SecretMove));
        lOffset = 86;
        tOffset = 75;
        g2d.drawString(currentDrawText,xAcross + lOffset, tOffset);
        currentDrawText = String.valueOf(playerTickets.get(Ticket.DoubleMove));
        lOffset = 70;
        tOffset = 85;
        g2d.drawString(currentDrawText,xAcross + lOffset, tOffset);
    }

    private void drawUndergroundNumber(int xAcross, Graphics2D g2d, String currentDrawText) {
        int lOffset;
        int tOffset;
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 58;
            tOffset = 75;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 56;
            tOffset = 82;
        } else {
            lOffset = 56;
            tOffset = 82;
        }

        g2d.drawString(currentDrawText,xAcross + lOffset, tOffset);
    }

    private void drawTaxiNumber(int xAcross, Graphics2D g2d, String currentDrawText) {
        int lOffset;
        int tOffset;
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 86;
            tOffset = 55;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 80;
            tOffset = 53;
        } else {
            lOffset = 80;
            tOffset = 53;
        }

        g2d.drawString(currentDrawText,xAcross + lOffset, tOffset);
    }

    private void drawBusNumber(int xAcross, Graphics2D g2d, FontMetrics fm, String currentDrawText) {
        int w,h,lOffset,tOffset;
        w = fm.stringWidth(currentDrawText);
        h = fm.getAscent();
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            lOffset = 55;
            tOffset = 52;
        } else if(mPlayerNumber % 2 == 0) {
            lOffset = 56;
            tOffset = 55;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        } else {
            lOffset = 56;
            tOffset = 55;
            g2d.rotate(0, lOffset + (w / 2), tOffset + (h / 2));
        }

        g2d.drawString(currentDrawText,xAcross + lOffset, tOffset);
    }

    private void drawPlayerName(int xAcross, Graphics2D g2d, String playerName) {
        int tOffset;
        int lOffset;
        if(mCurrentPlayer == Constants.MR_X_COLOUR) {
            tOffset = 0;
            lOffset = 0;
        } else if(mPlayerNumber % 2 == 0) {
            tOffset = 28;
            lOffset = 45;
        } else {
            tOffset = 28;
            lOffset = 45;
        }

        g2d.drawString(playerName, xAcross + lOffset, tOffset);
    }
}
