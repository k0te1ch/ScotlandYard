package solution.views.map;

import scotlandyard.Ticket;
import solution.development.models.ViewPath;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rory on 14/03/15.
 */
public class MapPath {

    private final Stacked2DPath path;
    private final int nodeId1;
    private final int nodeId2;
    private final ArrayList<Ticket> tickets;
    private final Set<Ticket> availableTickets;
    private boolean hovered;

    public MapPath(ViewPath viewPath) {
        this.path = viewPath.path;
        this.nodeId1 = viewPath.id1;
        this.nodeId2 = viewPath.id2;
        this.tickets = viewPath.types;

        this.availableTickets = new HashSet<Ticket>();
    }

    public void drawBackground(Graphics2D g2d){
        if(isAvailable()) {
            g2d.setStroke(new BasicStroke(Stacked2DPath.LINE_WIDTH * (2+availableTickets.size())));
            g2d.setColor(Color.darkGray);
        }else{

            g2d.setStroke(new BasicStroke(Stacked2DPath.LINE_WIDTH * (1 + tickets.size())));
            g2d.setColor(Color.WHITE);
        }

        g2d.draw(path.getPath());

    }

    public void draw(Graphics2D g2d) {

//        if(!available) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//        }
            path.draw(g2d);
//        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

    }



    public void notifyPositionHovered(MapPosition position) {
        hovered = position != null && hasNode(position.getId());
    }

    public boolean isAvailable() {
        return availableTickets != null && availableTickets.size() > 0;
    }

    public boolean hasNode(int nodeId) {
        return nodeId == nodeId1 || nodeId == nodeId2;
    }

    public Path2D getPath() {
        return path.getPath();
    }

    public int getStartingNode() {
        return nodeId1;
    }

    public int getEndingNode() {
        return nodeId2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPath mapPath = (MapPath) o;

        if (nodeId1 != mapPath.nodeId1) return false;
        if (nodeId2 != mapPath.nodeId2) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeId1;
        result = 31 * result + nodeId2;
        return result;
    }

    public boolean isHighlighted() {
        return isAvailable() && hovered;
    }


    public void resetAvailableTickets() {
        availableTickets.clear();
    }

    public void addAvailableTicket(Ticket availableTicket) {
        availableTickets.add(availableTicket);
        path.setTickets(new ArrayList<Ticket>(availableTickets));
    }
}
