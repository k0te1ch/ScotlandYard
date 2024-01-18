package solution.views;

import solution.helpers.SoundHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by rory on 10/03/15.
 */
public class PlayerAddView extends JPanel {

    private final int mMinPlayers;
    private final int mMaxPlayers;
    private Font mFont;
    private BufferedImage mDoneImg;
    private BufferedImage mLogoImg = null;
    private BufferedImage mPlayerAddImg = null;
    private PlayerCountListener mListener;
    private Ellipse2D mPlusBtn = null;
    private Ellipse2D mMinusBtn = null;
    private Rectangle mConfirmBtn = null;
    private boolean mShowMinusOverlay = false;
    private boolean mShowPlusOverlay = false;
    private boolean mShowConfirmOverlay = false;
    private int numberOfPlayers;

    class LocalMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            // If click and the max players hasnt been reached then increment the counter
            if (mPlusBtn.contains(e.getX(), e.getY()) ) {
                if(numberOfPlayers < mMaxPlayers){
                    SoundHelper.itemClick();
                    numberOfPlayers++;
                }else {
                    SoundHelper.itemNoClick();
                }
                repaint();
            }
            // If click and the min number of players hasnt been reached then decrement the counter
            if (mMinusBtn.contains(e.getX(), e.getY()) ) {
                if(numberOfPlayers > mMinPlayers){
                    SoundHelper.itemClick();
                    numberOfPlayers--;
                } else {
                    SoundHelper.itemNoClick();
                }
                repaint();
            }
            // If confirm button click then change slides
            if (mConfirmBtn.contains(e.getX(), e.getY()) ) {
                SoundHelper.itemClick();
                mListener.onPlayerCountDecided(numberOfPlayers);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);

            // If overlay on buttons the produce rectangle
            if (mPlusBtn.contains(e.getX(), e.getY()) ) {

                // Set variables
                mShowPlusOverlay = true;
                mShowMinusOverlay = false;
                mShowConfirmOverlay = false;

                // Call sound
                SoundHelper.itemHover();
                repaint();
            }else if (mMinusBtn.contains(e.getX(), e.getY()) ) {

                // Set variables
                mShowMinusOverlay = true;
                mShowPlusOverlay = false;
                mShowConfirmOverlay = false;

                // Call sound
                SoundHelper.itemHover();
                repaint();
            }else if (mConfirmBtn.contains(e.getX(), e.getY()) ) {

                // Set variables
                mShowMinusOverlay = false;
                mShowPlusOverlay = false;
                mShowConfirmOverlay = true;

                // Call sound
                SoundHelper.itemHover();
                repaint();
            } else {

                // Set variables
                mShowPlusOverlay = false;
                mShowMinusOverlay = false;
                mShowConfirmOverlay = false;

                // Call sound
                SoundHelper.itemDeHover();
                repaint();
            }
        }
    }
    public interface PlayerCountListener {
        public void onPlayerCountDecided(final int count);
    }
    public PlayerAddView(final int minPlayers, final int maxPlayers) {
        setOpaque(false);

        /// Set number of players
        mMinPlayers = minPlayers;
        mMaxPlayers = maxPlayers;
        numberOfPlayers = minPlayers;

        // Load in assets
        loadInImages();

        // Load in the font
        loadInFont();

        // Add listeners
        addMouseListener(new LocalMouseAdapter());
        addMouseMotionListener(new LocalMouseAdapter());
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
        URL resourceLogo = getClass().getClassLoader().getResource("ui" + File.separator + "logo.png");
        URL resourceNoP = getClass().getClassLoader().getResource("ui" + File.separator + "nop.png");
        URL resourceDoneBtn = getClass().getClassLoader().getResource("ui" + File.separator + "donebtn.png");
        try {
            mLogoImg = ImageIO.read(new File(resourceLogo.toURI()));
            mPlayerAddImg = ImageIO.read(new File(resourceNoP.toURI()));
            mDoneImg = ImageIO.read(new File(resourceDoneBtn.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        // Get width and height
        int w = getWidth();
        int h = getHeight();

        // Draw assets
        g2d.drawImage(mLogoImg, null, (w / 2) - (mLogoImg.getWidth() / 2), (h / 4) - (mLogoImg.getHeight() / 2));
        g2d.drawImage(mPlayerAddImg, null, (w / 2) - (mPlayerAddImg.getWidth() / 2), ((2*h) / 4));
        g2d.drawImage(mDoneImg, null, (w / 2) - (mDoneImg.getWidth() / 2), ((3*h) / 4));

        // Set up rectangle overlays
        mPlusBtn = new Ellipse2D.Double(
                (w / 2) + 123,
                ((2*h) / 4) + 119,
                50,
                50
        );
        mMinusBtn = new Ellipse2D.Double(
                (w / 2) - 172,
                ((2*h) / 4) + 120,
                50,
                50
        );
        mConfirmBtn = new Rectangle(
                (w / 2) - (mDoneImg.getWidth() / 2),
                ((3*h) / 4),
                mDoneImg.getWidth(),
                mDoneImg.getHeight()
        );

        // Draw player counter
        String numberOfPlayersStr = String.valueOf(numberOfPlayers);
        g2d.setFont(mFont.deriveFont(80f));
        FontMetrics fm = g.getFontMetrics();
        int wText = fm.stringWidth(numberOfPlayersStr);
        g2d.drawString(numberOfPlayersStr, (w / 2) - (wText / 2), ((2*h) / 4) + 160);


        // Draw rectangle overlays
        Color color = new Color(0, 0, 0, 0.1f);
        g2d.setPaint(color);
        if(mShowMinusOverlay){
            g2d.fill(mMinusBtn);
        }
        if(mShowPlusOverlay){
            g2d.fill(mPlusBtn);
        }
        if(mShowConfirmOverlay){
            g2d.fill(mConfirmBtn);
        }

    }

    public void setListener(PlayerCountListener listener){
        mListener = listener;
    }
}
