package solution.helpers;

import scotlandyard.Ticket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rory on 10/03/15.
 */
public class SetupHelper {

    public final static Ticket[] ticketTypes = { Ticket.Taxi, Ticket.Bus,
            Ticket.Underground, Ticket.DoubleMove,
            Ticket.SecretMove };

    public final static int[] mrXTicketNumbers = { 0, 0, 0, 2, 5 };
    public final static int[] detectiveTicketNumbers = { 11, 8, 4, 0, 0 };

    public static Map<Ticket, Integer> getTickets(boolean mrX)
    {
        Map<Ticket, Integer> tickets = new HashMap<Ticket, Integer>();
        for (int i = 0; i < ticketTypes.length; i++) {
            if(mrX)
                tickets.put(ticketTypes[i], mrXTicketNumbers[i]);
            else
                tickets.put(ticketTypes[i], detectiveTicketNumbers[i]);
        }
        return tickets;
    }

}
