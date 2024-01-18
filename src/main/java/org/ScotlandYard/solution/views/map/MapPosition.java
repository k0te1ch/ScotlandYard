package solution.views.map;

import scotlandyard.Ticket;
import solution.development.models.ViewPosition;
import solution.helpers.ColourHelper;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


/**
 * Created by rory on 11/03/15.
 */
public class MapPosition {

    public static final int CIRC_RADIUS = 20;
    private static final Color STANDARD_COLOUR = Color.GRAY;
    private static final Color HIGHLIGHT_COLOUR = Color.CYAN;
    private static final Color AVAILABLE_COLOUR = Color.MAGENTA;

    private final Integer positionId;
    private final int x;
    private final int y;
    private final Rectangle2D.Double rect;
    private final ArrayList<Ticket> tickets;
    private final int[] segmentStartAngles;
    private final int segmentAngleSize;
    private boolean hovered;
    private boolean available;
    private boolean highlighted;
    private Color playerColor;

    private int playerRingRadius = CIRC_RADIUS;
    private float playerRingAlpha = 1f;

    public MapPosition(ViewPosition viewPosition) {
        this.positionId = viewPosition.id;
        this.x = viewPosition.x;
        this.y = viewPosition.y;
        this.tickets = viewPosition.types;
        this.rect = new Rectangle2D.Double(x - CIRC_RADIUS/2,y - CIRC_RADIUS/2,CIRC_RADIUS,CIRC_RADIUS);

        segmentStartAngles = new int[tickets.size()];
        segmentAngleSize = (int) (360/(float)(segmentStartAngles.length));
        for (int i = 0; i < segmentStartAngles.length; i++) {
            segmentStartAngles[i] = i* segmentAngleSize;
        }

    }

    public void draw(final Graphics2D g2d){

        int radius = hovered && available ? (int) (CIRC_RADIUS * 1.5f) : CIRC_RADIUS;



        if(available){
            g2d.setColor(new Color(0, 0, 0, 187));
        }else if (hasPlayerColor()){
            g2d.setColor(new Color(playerColor.getRed(),playerColor.getGreen(),playerColor.getBlue()));
        }else{
            g2d.setColor(new Color(0, 0, 0, 107));
        }


        int outerRadius = (int) (radius*1.3f);
        g2d.fillOval(x - outerRadius / 2, y - outerRadius / 2, outerRadius, outerRadius);

        if(hasPlayerColor()){

            g2d.setColor(new Color(playerColor.getRed(),playerColor.getGreen(),playerColor.getBlue(),(int)(playerRingAlpha*255)));

            g2d.drawOval(x - playerRingRadius / 2, y - playerRingRadius / 2, playerRingRadius, playerRingRadius);

        }

        float brightness = 1f;
        if(!available && !hasPlayerColor()){
            brightness = 0.7f;
        }

        for (int i = 0; i < tickets.size(); i++) {
            Color color = ColourHelper.ticketColour(tickets.get(i));


            float[] hsb = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null);

            g2d.setColor(Color.getHSBColor(hsb[0], hsb[1], hsb[2]*brightness));
            g2d.fillArc(x - radius / 2, y - radius / 2, radius, radius, segmentStartAngles[i], segmentAngleSize);
        }


    }

    public boolean notifyMouseMove(int x, int y) {
        hovered = rect.contains(x,y);
        return hovered;
    }

    public boolean notifyMouseClick(int x, int y) {
        return rect.contains(x,y) && available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPosition position = (MapPosition) o;

        if (x != position.x) return false;
        if (y != position.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public int getId() {
        return positionId;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setPlayerColor(Color color){
        playerColor = color;
    }

    public boolean hasPlayerColor(){
        return playerColor != null;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public int getPlayerRingRadius() {
        return playerRingRadius;
    }

    public void setPlayerRingRadius(int playerRingRadius) {
        this.playerRingRadius = playerRingRadius;
    }

    public float getPlayerRingAlpha() {
        return playerRingAlpha;
    }

    public void setPlayerRingAlpha(float playerRingAlpha) {
        this.playerRingAlpha = playerRingAlpha;
    }

    public Color getPlayerColor() {
        return playerColor;
    }
}
