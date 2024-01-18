package org.ScotlandYard.solution.development.models;

import org.ScotlandYard.objects.Ticket;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * This is the view representation of a point on the map
 * x and y are its coordinates
 * id is its identifying number
 * types is a list of the types of route that start or finish at this position
 */
public class ViewPosition {
    public int x;
    public int y;
    public int id;
    public ArrayList<Ticket> types;

    public ViewPosition(DataPosition dataPosition) {
        this.x = dataPosition.x;
        this.y = dataPosition.y;
        this.id = dataPosition.id;

        this.types = new ArrayList<Ticket>();
    }


    public void setTypes(ArrayList<Ticket> ticketTypes) {
        types = new ArrayList<Ticket>(new LinkedHashSet<Ticket>(ticketTypes));
    }
}
