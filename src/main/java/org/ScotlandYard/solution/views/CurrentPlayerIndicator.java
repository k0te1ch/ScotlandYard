package solution.views;

import scotlandyard.Colour;
import solution.helpers.ColourHelper;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by rory on 10/03/15.
 */
public class CurrentPlayerIndicator extends JPanel {

    private static final int OUTER_RING_RADIUS = 20;
    private static final int INNER_CIRC_RADIUS = 10;
    private static final int STROKE_WIDTH = (OUTER_RING_RADIUS - INNER_CIRC_RADIUS)/2;
    private List<Colour> mColours;
    private final Dimension mSize;
    private Colour mSelectedColour;

    public CurrentPlayerIndicator () {
        mColours = new ArrayList<Colour>();
        mSize = new Dimension(mColours.size() * OUTER_RING_RADIUS * 2, OUTER_RING_RADIUS *2);

        setSize(mSize);
    }

    public void setColours(final java.util.List<Colour> colours){
        mColours = colours;
    }

    public void setSelectedColour(Colour selectedColour){
        mSelectedColour = selectedColour;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(STROKE_WIDTH));

        for(int i=0; i<mColours.size(); i++){
            final Colour colour = ColourHelper.getColour(i);

            int correctedOuterRingRadius = OUTER_RING_RADIUS - STROKE_WIDTH;

            g2d.setColor(ColourHelper.toColor(colour));
            if(mSelectedColour != null && mSelectedColour.equals(colour)){
                g2d.drawOval(STROKE_WIDTH / 2 + i * correctedOuterRingRadius * 2, STROKE_WIDTH / 2, correctedOuterRingRadius * 2, correctedOuterRingRadius * 2);
            }

            final int padding = (correctedOuterRingRadius - INNER_CIRC_RADIUS) + STROKE_WIDTH/2;
            g2d.fillOval(padding + i * correctedOuterRingRadius * 2, padding, INNER_CIRC_RADIUS * 2, INNER_CIRC_RADIUS * 2);
        }

    }
}
