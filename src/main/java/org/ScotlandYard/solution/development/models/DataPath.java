package org.ScotlandYard.solution.development.models;

import org.ScotlandYard.solution.development.MapCanvas;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class DataPath {
    public int id1;
    public int id2;
    public int[] pathXCoords;
    public int[] pathYCoords;

    private transient int movingCoordIndex = -1;

    public DataPath(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }
    
    public Path2D getPath(){
        Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD, pathXCoords.length);
        for (int i = 0; i < pathXCoords.length; i++) {
            if (i == 0) {
                path.moveTo(pathXCoords[i], pathYCoords[i]);
            } else {
                path.lineTo(pathXCoords[i], pathYCoords[i]);
            }
        }
        return path;
    }

    /**
     * This is called when the DataPath is selected by a user, it will
     * add a point along the path which can be moved later by the user
     * as a waypoint
     *
     * @param x the x position of the click
     * @param y the y position of the click
     */
    public void onSelected(int x, int y) {

        //first we check to see whether or not the user is clicking an existing waypoint on our path
        //if they are, we set that waypoint's index as the currently moving index, movingCoordIndex
        for (int i = 0; i < pathXCoords.length; i++) {
            Rectangle2D.Double rect = new Rectangle2D.Double(pathXCoords[i] - MapCanvas.EDIT_POINT_CIRC_SIZE / 2, pathYCoords[i] - MapCanvas.EDIT_POINT_CIRC_SIZE / 2, MapCanvas.EDIT_POINT_CIRC_SIZE, MapCanvas.EDIT_POINT_CIRC_SIZE);
            if(rect.contains(x,y)){
                movingCoordIndex = i;
                return;
            }
        }

        //if we're here then we will be adding a new position
        PathIterator iterator = getPath().getPathIterator(null);

        float[] prevCoords = null;

        movingCoordIndex = 0;

        //we iterate through the path's segments until we find the segment that has been clicked
        //on each iteration we keep track of the index we're at as we need to know how far along
        //the path we are in order to insert the new point in the correct position
        while(!iterator.isDone()){

            float[] curCoords = new float[2];

            iterator.currentSegment(curCoords);

            if(prevCoords != null){
                Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD,2);
                path.moveTo(prevCoords[0],prevCoords[1]);
                path.lineTo(curCoords[0], curCoords[1]);
                if(new BasicStroke(3f).createStrokedShape(path).contains(x,y)){
                    break;
                }
            }

            prevCoords = curCoords;

            movingCoordIndex++;
            iterator.next();
        }

        //now that we know where to insert the new point we will create a new list
        //of x and y coordinates and insert the new point in it
        int[] newXCoords = new int[pathXCoords.length+1];
        int[] newYCoords = new int[pathYCoords.length+1];

        for (int i = 0; i < pathXCoords.length + 1; i++) {
            if(i == movingCoordIndex){
                newXCoords[i] = x;
                newYCoords[i] = y;
            }else if(i < movingCoordIndex){
                newXCoords[i] = pathXCoords[i];
                newYCoords[i] = pathYCoords[i];
            }else{
                newXCoords[i] = pathXCoords[i-1];
                newYCoords[i] = pathYCoords[i-1];
            }
        }

        pathXCoords = newXCoords;
        pathYCoords = newYCoords;

    }

    /**
     *
     * This updates the x and y coordinates of the selected coordinate
     *
     * @param x the x position of the click
     * @param y the y position of the click
     */
    public void onPointDrag(int x, int y) {
        pathXCoords[movingCoordIndex] = x;
        pathYCoords[movingCoordIndex] = y;
    }

    /**
     * Clears the {@link solution.development.models.DataPath#movingCoordIndex}
     */
    public void onDragStop() {
        movingCoordIndex = -1;
    }
}
