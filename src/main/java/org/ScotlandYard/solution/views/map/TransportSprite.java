package solution.views.map;

import scotlandyard.Ticket;
import solution.helpers.PathInterpolator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by rory on 15/03/15.
 */
public class TransportSprite {

    private Image image;
    private int x;
    private int y;
    private float rotation;


    public TransportSprite(Ticket ticketType) {


            String filePath = null;
            switch (ticketType) {
                case Taxi:
                    filePath = "imgs/actual_taxi.png";
                    break;
                case Bus:
                    filePath = "imgs/actual_bus.png";
                    break;
                case Underground:
                    filePath = "imgs/actual_train.png";
                    break;
                case SecretMove:
                    filePath = "imgs/actual_secret.png";
                    break;
            }

        try {
            image = loadImage(new File(getClass().getClassLoader().getResource(filePath).toURI()), MapPosition.CIRC_RADIUS);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Image loadImage(File file, int height) throws IOException {
        final BufferedImage image = ImageIO.read(file);
        float scale = (height) / (float) image.getHeight();
        final int targetImageWidth = (int) (image.getWidth() * scale);
        final int targetImageHeight = (int) (image.getHeight() * scale);
        return image.getScaledInstance(targetImageWidth, targetImageHeight, 0);
    }

    public void draw(Graphics2D g2d){

        int xPos = x - image.getWidth(null)/2;
        int yPos = y - image.getHeight(null)/2;
        double theta = Math.PI / 2 + rotation;
        g2d.rotate(theta, x, y);
        g2d.drawImage(image, xPos, yPos, null);
        g2d.rotate(-theta, x, y);


    }

    public void setSegment(PathInterpolator.Segment segment){
        this.x = (int) segment.getX();
        this.y = (int) segment.getY();
        this.rotation = segment.getRotation();
    }

}
