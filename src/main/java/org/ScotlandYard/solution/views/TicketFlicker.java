package solution.views;

import scotlandyard.Colour;
import scotlandyard.MoveTicket;
import scotlandyard.Ticket;
import solution.interfaces.TicketImageHolder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by benallen on 20/03/15.
 */
public class TicketFlicker {
    public void draw(List<MoveTicket> mrXHistory, int mAlternator, HashMap<Ticket, BufferedImage> ticketToImg, int sideBarWidth, int tempYPosTicket, Graphics2D g2d){
        // Ticket Flicker
        int initOffset = 270; // the initial offset from the top
        int midDifference = 95; // height of a ticket

        // Number of elements on stack
        int firstStack = mrXHistory.size() + mAlternator;
        int secondStack = mrXHistory.size() - firstStack - 1;

        // Init the stacks top, middle and bottom
        TicketImageHolder[] topStack;
        TicketImageHolder[] bottomStack;
        TicketImageHolder workingTicket = new TicketImageHolder();

        // Init the first stack
        if(firstStack > 0){
            topStack = new TicketImageHolder[firstStack];
        } else {
            topStack = new TicketImageHolder[0];
        }
        // Init the second stack
        if(secondStack > 0) {
            bottomStack = new TicketImageHolder[secondStack];
        } else {
            bottomStack = new TicketImageHolder[0];
        }

        // Init the margin and yPosition offsets
        int ticketsIteratorTop = 0;
        int ticketsIteratorBottom = 0;
        int topStackOffset = initOffset;
        int midStackOffset = initOffset + midDifference;
        int bottomStackOffset = midStackOffset + midDifference;

        // Add the tickets into the holders
        for (int i = 0; i < mrXHistory.size(); i++){

            // Get the working ticket content
            MoveTicket t = mrXHistory.get(i);
            BufferedImage thisImg = ticketToImg.get(t.ticket);

            // Add the tickets to there stacks
            if(i < firstStack){
                // Add image onto the first stack
                topStackOffset += 2;
                topStack[ticketsIteratorTop] = new TicketImageHolder();
                topStack[ticketsIteratorTop].setImg(thisImg);
                topStack[ticketsIteratorTop].setxPos((sideBarWidth / 2) - (thisImg.getWidth() / 2));
                topStack[ticketsIteratorTop].setyPos(topStackOffset);
                ticketsIteratorTop++;
            } else if(i == firstStack) {
                // Add middle stack
                workingTicket.setImg(thisImg);
                workingTicket.setyPos(midStackOffset - tempYPosTicket);
                workingTicket.setxPos((sideBarWidth / 2) - (thisImg.getWidth() / 2));

            } else {
                // Add the ticket onto the bottom stack
                bottomStackOffset += 2;
                bottomStack[ticketsIteratorBottom] = new TicketImageHolder();
                bottomStack[ticketsIteratorBottom].setImg(thisImg);
                bottomStack[ticketsIteratorBottom].setxPos((sideBarWidth / 2) - (thisImg.getWidth() / 2));
                bottomStack[ticketsIteratorBottom].setyPos(bottomStackOffset);
                ticketsIteratorBottom++;
            }


        }

        // Move top stacks top ticket
        if(topStack.length > 0){
            // Only move the current y is less than the minimum
            int topItemNumber = topStack.length - 1;
            int currentY;
            if(topStack[topItemNumber] != null){
                currentY = topStack[topItemNumber].getyPos();
                int newYpos = currentY - tempYPosTicket;

                //If the Y position of the item is greater than the minimum then we can change its position
                if(newYpos >= topStackOffset) {
                    topStack[topItemNumber].setyPos(newYpos);
                }
            }

        }

        // Move the bottom stacks top ticket
        if(bottomStack.length > 0){
            int currentY;
            if(bottomStack[0] != null){
                currentY = bottomStack[0].getyPos();
                int newYpos = currentY - tempYPosTicket;
                if(newYpos <= bottomStackOffset) {
                    bottomStack[0].setyPos(newYpos);
                }
            }
        }

        // Draw the first stack of tickets
        for(int i = 0; i < topStack.length; i++){
            if(topStack[i] != null) {
                topStack[i].draw(g2d);
            }
        }

        // Draw the second stack of tickets in reverse
        for(int i = bottomStack.length - 1; i >= 0; i--){
            if(bottomStack[i] != null){
                bottomStack[i].draw(g2d);
            }
        }

        // Draw the middle ticket
        workingTicket.draw(g2d);
    }
    public List<MoveTicket> createExampleTickets(){

        // Temporary tickets for testing
        List<MoveTicket> mrXHistory = new ArrayList<MoveTicket>();

        MoveTicket m = new MoveTicket(Colour.Black, 10, Ticket.DoubleMove);
        mrXHistory.add(m);
        m = new MoveTicket(Colour.Black, 10, Ticket.SecretMove);
        mrXHistory.add(m);
        m = new MoveTicket(Colour.Black, 10, Ticket.DoubleMove);
        mrXHistory.add(m);
        m = new MoveTicket(Colour.Black, 10, Ticket.SecretMove);
        mrXHistory.add(m);
        m = new MoveTicket(Colour.Black, 10, Ticket.DoubleMove);
        mrXHistory.add(m);
        return mrXHistory;
    }
}
