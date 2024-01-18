package org.ScotlandYard.solution.Models;

import com.google.gson.Gson;
import org.ScotlandYard.objects.Ticket;
import org.ScotlandYard.solution.development.models.*;
import org.ScotlandYard.solution.views.map.MapPath;
import org.ScotlandYard.solution.views.map.MapPosition;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MapData {

    public enum DataFormat {STANDARD, CUSTOM}

    private static final String TXT = "txt";
    private static final String JSON = "json";

    private final ArrayList<MapPosition> mPositionList;
    private final ArrayList<MapPath> mPathList;
    private final ArrayList<ViewRoute> mRouteList;

    public MapData(final String dataFilePath, final DataFormat dataFormat){

        final String extension = dataFilePath.substring(dataFilePath.lastIndexOf(".")+1);

        mPositionList = new ArrayList<MapPosition>();
        mPathList = new ArrayList<MapPath>();
        mRouteList = new ArrayList<ViewRoute>();

        if(dataFormat == DataFormat.STANDARD){
            parseStandardTextFile(dataFilePath);
        }else if(dataFormat == DataFormat.CUSTOM){
            parseCustomTextFile(dataFilePath);
        }else{
            System.err.println("unknown data format: "+extension);
        }
    }

    @Deprecated
    private void parseStandardTextFile(String filePath) {

//        try {
//
//            final URL resource = getClass().getClassLoader().getResource(filePath);
//            for(String line : Files.readAllLines(Paths.get(resource.toURI()))){
//                String[] pieces = line.split(" ");
//                if(pieces.length < 3) {
//                    System.err.println("bad line: "+line);
//                }else{
//                    mPositionList.add(new MapPosition(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]), Integer.parseInt(pieces[2])));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    private void parseCustomTextFile(String filePath) {

        String input = null;
        try {
            final URL resource = getClass().getClassLoader().getResource(filePath);
            assert resource != null;
            input = StringUtils.join(Files.readAllLines(Paths.get(resource.toURI())), "");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        DataSave dataSave = gson.fromJson(input, DataSave.class);


        mRouteList.clear();
        mPositionList.clear();
        mPathList.clear();

        for (DataRoute dataRoute : dataSave.routeList) {
            mRouteList.add(new ViewRoute(dataRoute, dataSave.positionList, dataSave.pathList));
        }

        for (DataPosition dataPosition : dataSave.positionList) {
            ViewPosition viewPosition = new ViewPosition(dataPosition);


            ArrayList<Ticket> ticketTypes = new ArrayList<Ticket>();
            for (ViewRoute viewRoute : mRouteList) {
                if (viewRoute.positionList.get(0).id == dataPosition.id || viewRoute.positionList.get(viewRoute.positionList.size() - 1).id == dataPosition.id) {
                    ticketTypes.add(viewRoute.type);
                }
            }

            viewPosition.setTypes(ticketTypes);

            mPositionList.add(new MapPosition(viewPosition));
        }

        for (DataPath dataPath : dataSave.pathList) {
            ViewPath viewPath = new ViewPath(dataPath);


            ArrayList<Ticket> ticketTypes = new ArrayList<Ticket>();

            for (ViewRoute viewRoute : mRouteList) {
                for (int i = 0; i < viewRoute.positionList.size() - 1; i++) {
                    if (viewRoute.positionList.get(i).id == dataPath.id1 && viewRoute.positionList.get(i + 1).id == dataPath.id2
                            || viewRoute.positionList.get(i).id == dataPath.id2 && viewRoute.positionList.get(i + 1).id == dataPath.id1) {
                        ticketTypes.add(viewRoute.type);
                    }

                }


            }

            viewPath.setTypes(ticketTypes);

            mPathList.add(new MapPath(viewPath));

        }

    }

    public ArrayList<MapPosition> getPositionList() {
        return mPositionList;
    }

    public ArrayList<MapPath> getPathList() {
        return mPathList;
    }

    public ArrayList<ViewRoute> getRouteList() {
        return mRouteList;
    }
}
