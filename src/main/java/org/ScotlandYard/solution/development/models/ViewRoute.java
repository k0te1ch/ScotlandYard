package org.ScotlandYard.solution.development.models;

import org.ScotlandYard.objects.Ticket;
import org.ScotlandYard.solution.development.ShortestPathHelper;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This is the view representation of a route along 1 or more DataPaths
 * both ids represent associated DataPosition ids, unordered
 * path describes the entire collection of coordinates from id1 to id2 (or vice versa)
 * positionList is the list of DataPositions the DataRoute passes through
 **/
public class ViewRoute {
    public int id1;
    public int id2;
    public Path2D path;
    public Ticket type;
    public ArrayList<DataPosition> positionList;

    public ViewRoute(DataRoute dataRoute, ArrayList<DataPosition> dataPositions, ArrayList<DataPath> dataPaths) {

        this.type = dataRoute.type;

        this.id1 = dataRoute.waypointIdList.get(0);
        this.id2 = dataRoute.waypointIdList.get(dataRoute.waypointIdList.size()-1);

        LinkedHashSet<DataPosition> pathSet = new LinkedHashSet<DataPosition>();

        for (int i = 0; i < dataRoute.waypointIdList.size()-1; i++) {
            ArrayList<DataPosition> list = ShortestPathHelper.shortestPath(dataRoute.waypointIdList.get(i), dataRoute.waypointIdList.get(i + 1), dataPositions, dataPaths);
            for (int j = list.size()-1; j >= 0; j--) {
                DataPosition position = list.get(j);
                pathSet.add(position);
            }
        }

        positionList = new ArrayList<DataPosition>(pathSet);

        ArrayList<DataPath> pathList = new ArrayList<DataPath>();



        int lastDataId = -1;
        for(DataPosition dataPosition : positionList){
            if(lastDataId != -1) {
                for(DataPath dataPath : dataPaths){
                    if((dataPath.id1 == lastDataId && dataPath.id2 == dataPosition.id)
                            || (dataPath.id2 == lastDataId && dataPath.id1 == dataPosition.id)){
                        pathList.add(dataPath);
                        break;
                    }
                }
            }
            lastDataId = dataPosition.id;
        }


        ArrayList<Integer> xs = new ArrayList<Integer>();
        ArrayList<Integer> ys = new ArrayList<Integer>();

        int searchId = id1;
        while (pathList.size() > 0){
            DataPath chosenPath = null;
            for(DataPath dataPath : pathList){
                    int[] pathXCoords = dataPath.pathXCoords;
                    int[] pathYCoords = dataPath.pathYCoords;
                if(dataPath.id1 == searchId){

                    for (int i = 0; i < pathXCoords.length; i++) {
                        int x = pathXCoords[i];
                        int y = pathYCoords[i];

                        xs.add(x);
                        ys.add(y);

                    }
                    chosenPath = dataPath;
                    searchId = chosenPath.id2;
                    break;
                }else if(dataPath.id2 == searchId){

                    for (int i = pathXCoords.length-1; i >= 0; i--) {
                        int x = pathXCoords[i];
                        int y = pathYCoords[i];

                        xs.add(x);
                        ys.add(y);

                    }
                    chosenPath = dataPath;
                    searchId = chosenPath.id1;
                    break;
                }
            }
            pathList.remove(chosenPath);
        }

        this.path = new Path2D.Double(Path2D.WIND_EVEN_ODD, positionList.size());

        int prevX = 0;
        int prevY = 0;

        int pathCount = 0;
        for (int i = 0; i < xs.size(); i++) {
            int x = xs.get(i);
            int y = ys.get(i);

            if(prevX != x || prevY != y){
                prevX = x;
                prevY = y;
                if(pathCount == 0){
                   path.moveTo(x,y);
                }else{
                    path.lineTo(x,y);
                }
                pathCount++;
            }


        }


    }
}
