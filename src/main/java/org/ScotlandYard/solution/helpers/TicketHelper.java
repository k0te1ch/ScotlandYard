package solution.helpers;

import scotlandyard.Ticket;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by benallen on 12/03/15.
 */
public class TicketHelper {
    /*
    *    Converts a ticket into a imageIcon
    */
    public static ImageIcon ticketImgs(Ticket ticketName){
        HashMap<Ticket, String> ticketTypeTo = new HashMap<Ticket, String>(6);
        ticketTypeTo.put(Ticket.Bus, "bus.png");
        ticketTypeTo.put(Ticket.Underground, "underground.png");
        ticketTypeTo.put(Ticket.Taxi, "taxi.png");
        ticketTypeTo.put(Ticket.DoubleMove, "doublemove.png");
        ticketTypeTo.put(Ticket.SecretMove, "secretmove.png");

        String imgName = ticketTypeTo.get(ticketName);

        URL resource = ticketName.getClass().getClassLoader().getResource("imgs" + File.separator + imgName);
        ImageIcon ticketIcon = new ImageIcon(resource);

        return ticketIcon;

    }
    /*
    *    Converts a ticket into a buffered image
    */
    public static BufferedImage ticketBuffImg(Ticket ticketName){
        HashMap<Ticket, String> ticketTypeTo = new HashMap<Ticket, String>(6);
        ticketTypeTo.put(Ticket.Bus, "busticket.png");
        ticketTypeTo.put(Ticket.Underground, "undergroundticket.png");
        ticketTypeTo.put(Ticket.Taxi, "taxiticket.png");
        ticketTypeTo.put(Ticket.DoubleMove, "doubleticket.png");
        ticketTypeTo.put(Ticket.SecretMove, "secretticket.png");

        String imgName = ticketTypeTo.get(ticketName);

        URL resource = ticketName.getClass().getClassLoader().getResource("ui" + File.separator + imgName);
        BufferedImage ticketImg = null;
        try {
            ticketImg = ImageIO.read(new File(resource.toURI()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ticketImg;

    }
}
