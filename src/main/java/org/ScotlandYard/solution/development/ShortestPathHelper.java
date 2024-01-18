package org.ScotlandYard.solution.development;

import org.ScotlandYard.solution.development.models.DataPath;
import org.ScotlandYard.solution.development.models.DataPosition;

import java.util.ArrayList;

public class ShortestPathHelper {

    public static ArrayList<DataPosition> shortestPath(final int sourceId, final int targetId, final ArrayList<DataPosition> dataPositions, final ArrayList<DataPath> dataPaths){

        ArrayList<SearchHolder> fullList = new ArrayList<SearchHolder>();

        SearchHolder sourceHolder = null;

        for(DataPosition dataPosition : dataPositions){
            SearchHolder searchHolder = new SearchHolder(dataPosition);
            fullList.add(searchHolder);
            if(dataPosition.id == sourceId){
                sourceHolder = searchHolder;
            }
        }

        ArrayList<SearchHolder> queue = new ArrayList<SearchHolder>();

        sourceHolder.discovered = true;
        queue.add(sourceHolder);

        SearchHolder targetHolder = null;

        while(queue.size() > 0){
            SearchHolder searchTerm = queue.iterator().next();

            queue.remove(searchTerm);

            for(DataPath dataPath : dataPaths){
                if(dataPath.id1 == searchTerm.dataPosition.id){
                    for(SearchHolder holder : fullList){
                        if(holder.dataPosition.id == dataPath.id2 && !holder.discovered){
                            holder.previousSearchHolder = searchTerm;
                            holder.discovered = true;
                            if(holder.dataPosition.id == targetId){
                                targetHolder = holder;
                            }else {
                                queue.add(holder);
                            }
                            break;
                        }
                    }
                }else if(dataPath.id2 == searchTerm.dataPosition.id){
                    for(SearchHolder holder : fullList){
                        if(holder.dataPosition.id == dataPath.id1 && !holder.discovered){
                            holder.previousSearchHolder = searchTerm;
                            holder.discovered = true;

                            if(holder.dataPosition.id == targetId){
                                targetHolder = holder;
                            }else {
                                queue.add(holder);
                            }
                            break;
                        }
                    }
                }

                if(targetHolder != null){
                    break;
                }
            }

            if(targetHolder != null){
                break;
            }

        }

        ArrayList<DataPosition> positionsList = null;

        if(targetHolder != null){
            positionsList = new ArrayList<DataPosition>();

            while(targetHolder != null){
                positionsList.add(targetHolder.dataPosition);
                targetHolder = targetHolder.previousSearchHolder;
            }
        }

        if(positionsList == null){
            System.err.println("positionsList is null for "+sourceId+" -> "+targetId);
        }else if(positionsList.size() == 0){
            System.err.println("positionsList is empty for "+sourceId+" -> "+targetId);
        }



        return positionsList;


    }

    static class SearchHolder {

        public final DataPosition dataPosition;
        public boolean discovered;
        public SearchHolder previousSearchHolder;

        public SearchHolder(DataPosition dataPosition) {
            this.dataPosition = dataPosition;
        }
    }


}
