package solution.interfaces;

import java.awt.*;
import java.awt.image.BufferedImage;


public class TicketImageHolder {
    private BufferedImage img;
    private int yPos;
    private int xPos;

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }
    public void draw(Graphics2D g){
        g.drawImage(getImg(),null, getxPos(), getyPos());
    }

}
