package solution.views.map;

import scotlandyard.Ticket;
import solution.helpers.ColourHelper;
import solution.helpers.PathInterpolator;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

/**
 * Created by rory on 16/03/15.
 */
public class Stacked2DPath {

    public static final float LINE_WIDTH = 4f;
    private final ArrayList<Path2D> paths;
    private final Path2D mPath;
    private ArrayList<Ticket> mTickets;

    public Stacked2DPath(Path2D path, ArrayList<Ticket> tickets){
        paths = new ArrayList<Path2D>();
        mTickets = tickets;
        mPath = path;
        stackPath(path, mTickets.size());

    }

    private void stackPath(Path2D path, int lineCount) {

        paths.clear();

        PathInterpolator interpolator = new PathInterpolator(path);

        interpolator.interpolate(3);


        PathIterator iterator = path.getPathIterator(null);

        float[] coords = new float[2];

        ArrayList<Float> xs = new ArrayList<Float>();
        ArrayList<Float> ys = new ArrayList<Float>();

        while(!iterator.isDone()){
            iterator.currentSegment(coords);
            xs.add(coords[0]);
            ys.add(coords[1]);
            iterator.next();
        }

        float lineLength = (LINE_WIDTH/2f) * (lineCount - 1);


        for (int i = 0; i < lineCount; i++) {
            paths.add(new Path2D.Double());
        }

        int i = 0;
        while(i < xs.size()) {


            float curX = xs.get(i);
            float curY = ys.get(i);
            if (i == 0 && xs.size() > 1) {
                //we'll do from this point to half the next

                float nextX = xs.get(i + 1);
                float nextY = ys.get(i + 1);

                float halfNextX = curX + (nextX - curX) / 2f;
                float halfNextY = curY + (nextY - curY) / 2f;

                float firstDx = halfNextX - curX;
                float firstDy = halfNextY - curY;

                float firstNormalDx = firstDy;
                float firstNormalDy = -firstDx;

                double firstTheta = Math.atan2(firstNormalDy, firstNormalDx);

                for (int j = 0; j < lineCount; j++) {

                    float length = lineLength - (j * LINE_WIDTH);

                    double xPos = curX - length * Math.cos(firstTheta);
                    double yPos = curY - length * Math.sin(firstTheta);

                    double nextXPos = halfNextX - length * Math.cos(firstTheta);
                    double nextYPos = halfNextY - length * Math.sin(firstTheta);

                    paths.get(j).moveTo(xPos, yPos);
                    paths.get(j).lineTo(nextXPos, nextYPos);
                }

            } else if (i == xs.size() - 1 && xs.size() > 1) {
                //we'll do from half the previous to this point

                float prevX = xs.get(i - 1);
                float prevY = ys.get(i - 1);

                float halfPrevX = prevX + (curX - prevX) / 2f;
                float halfPrevY = prevY + (curY - prevY) / 2f;

                float firstDx = curX - halfPrevX;
                float firstDy = curY - halfPrevY;

                float firstNormalDx = firstDy;
                float firstNormalDy = -firstDx;

                double firstTheta = Math.atan2(firstNormalDy, firstNormalDx);

                for (int j = 0; j < lineCount; j++) {

                    float length = lineLength - (j * LINE_WIDTH);

                    double xPos = curX - length * Math.cos(firstTheta);
                    double yPos = curY - length * Math.sin(firstTheta);

                    paths.get(j).lineTo(xPos,yPos);
                }


            } else if (xs.size() > 2) {
                //we'll do from half previous to half next

                float nextX = xs.get(i + 1);
                float nextY = ys.get(i + 1);

                float halfNextX = curX + (nextX - curX) / 2f;
                float halfNextY = curY + (nextY - curY) / 2f;

                float prevX = xs.get(i - 1);
                float prevY = ys.get(i - 1);

                float halfPrevX = prevX + (curX - prevX) / 2f;
                float halfPrevY = prevY + (curY - prevY) / 2f;


                float secondDx = halfNextX - curX;
                float secondDy = halfNextY - curY;

                float secondNormalDx = secondDy;
                float secondNormalDy = -secondDx;

                double secondTheta = Math.atan2(secondNormalDy, secondNormalDx);


                float firstDx = curX - halfPrevX;
                float firstDy = curY - halfPrevY;

                float firstNormalDx = firstDy;
                float firstNormalDy = -firstDx;

                double firstTheta = Math.atan2(firstNormalDy, firstNormalDx);

                double alpha = (firstTheta + secondTheta) / 2f;

                for (int j = 0; j < lineCount; j++) {

                    float length = lineLength - (j * LINE_WIDTH);


                    double xOffset = length * Math.cos(firstTheta);
                    double yOffset = length * Math.sin(firstTheta);

                    double xPos = curX - xOffset;
                    double yPos = curY - yOffset;

                    paths.get(j).lineTo(xPos,yPos);



                    xOffset = length * Math.cos(secondTheta);
                    yOffset = length * Math.sin(secondTheta);

                    xPos = curX - xOffset;
                    yPos = curY - yOffset;

                    double nextXPos = halfNextX - lineLength * Math.cos(secondTheta) + j * (LINE_WIDTH * Math.cos(secondTheta));
                    double nextYPos = halfNextY - lineLength * Math.sin(secondTheta) + j * (LINE_WIDTH * Math.sin(secondTheta));

                    paths.get(j).lineTo(xPos, yPos);
                    paths.get(j).lineTo(nextXPos, nextYPos);
                }

            }
            i++;
        }

        }

    public void draw(Graphics2D g2d){

        g2d.setStroke(new BasicStroke(LINE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f));
        for (int i = 0; i < paths.size(); i++) {
            Path2D path2D = paths.get(i);

            g2d.setColor(ColourHelper.ticketColour(mTickets.get(i)));


            g2d.draw(path2D);
        }
    }

    public Path2D getPath() {
        return mPath;
    }

    public void setTickets(ArrayList<Ticket> availableTickets) {
        mTickets = availableTickets;
        stackPath(mPath, mTickets.size());
    }
}
