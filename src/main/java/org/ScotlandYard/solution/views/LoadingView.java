package solution.views;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by benallen on 18/03/15.
 */
public class LoadingView extends JPanel {
    private BufferedImage mLoadingImg;

    public LoadingView(){
        setOpaque(false);
        loadInAssets();
    }

    private void loadInAssets() {
        URL resource1 = getClass().getClassLoader().getResource("ui" + File.separator + "loadingimg.png");
        try {
            mLoadingImg = ImageIO.read(new File(resource1.toURI()));
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
        int w = getWidth();
        int h = getHeight();
        g2d.drawImage(mLoadingImg, null, (w / 2) - (mLoadingImg.getWidth() / 2), (h / 2) - (mLoadingImg.getHeight() / 2));

    }
}
