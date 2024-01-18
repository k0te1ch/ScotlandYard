package org.ScotlandYard.solution.helpers;

import scotlandyard.Colour;
import scotlandyard.Ticket;

import java.awt.*;

/**
 * This utility class contains a few methods used commonly throughout
 */
public class ColourHelper {

    public static Colour getColour(final int pos){
        switch (pos){
            case 0:
                return Colour.Black;
            case 1:
                return Colour.Blue;
            case 2:
                return Colour.Green;
            case 3:
                return Colour.Yellow;
            case 4:
                return Colour.White;
            case 5:
                return Colour.Red;
            default:
                throw new IllegalStateException("We don't have a color for position "+pos);
        }
    }

    public static Color ticketColour(Ticket ticket){
        switch (ticket){
            case Taxi:
                return new Color(253, 255, 0);
            case Bus:
                return Color.GREEN;
            case Underground:
                return Color.RED;
            case SecretMove:
                return Color.BLACK;
        }
        return Color.BLACK;
    }


    public static String toString(Colour colour) {
        switch (colour){
            case Black:
                return "Black";
            case Blue:
                return "Blue";
            case Green:
                return "Green";
            case Yellow:
                return "Yellow";
            case White:
                return "White";
            case Red:
                return "Red";
            default:
                return null;
        }
    }

    public static Color toColor(Colour colour) {
        switch (colour){
            case Black:
                return Color.black;
            case Blue:
                return Color.BLUE;
            case Green:
                return Color.GREEN;
            case Yellow:
                return Color.YELLOW;
            case White:
                return Color.WHITE;
            case Red:
                return Color.RED;
            default:
                return null;
        }
    }
}
