package org.ScotlandYard.solution.development.models;

import org.ScotlandYard.objects.Route;
import org.ScotlandYard.objects.Ticket;

import java.util.ArrayList;

public class DataRoute {
    public ArrayList<Integer> waypointIdList;
    public Ticket type;

    public DataRoute(Integer source, Integer target, Route route) {
        waypointIdList = new ArrayList<>();

        waypointIdList.add(source);
        waypointIdList.add(target);


        type = Ticket.fromRoute(route);
    }
}
