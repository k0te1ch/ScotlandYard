package org.ScotlandYard.solution.Models;

import org.ScotlandYard.objects.Move;
import org.ScotlandYard.objects.Ticket;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveData {

    HashMap<Color, Integer> startPositions;
    HashMap<Color, Map<Ticket, Integer>> tickets;
    String graphName;
    Move[] movesList;
    List<Boolean> rounds;
    List<Color> ColorList;

    public SaveData(Move[] moves, List<Boolean> rounds, List<Color> ColorList, String graphName, HashMap<Color, Map<Ticket, Integer>> tickets, HashMap<Color, Integer> startPositions) {
        movesList = moves;
        this.rounds = rounds;
        this.ColorList = ColorList;
        this.graphName = graphName;
        this.tickets = tickets;
        this.startPositions = startPositions;
    }

    public Move[] getMovesList() {
        return movesList;
    }

    public void setMovesList(Move[] movesList) {
        this.movesList = movesList;
    }

    public List<Boolean> getRounds() {
        return rounds;
    }

    public void setRounds(List<Boolean> rounds) {
        this.rounds = rounds;
    }

    public List<Color> getColorList() {
        return ColorList;
    }

    public void setColorList(List<Color> ColorList) {
        this.ColorList = ColorList;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public HashMap<Color, Map<Ticket, Integer>> getTickets() {
        return tickets;
    }

    public void setTickets(HashMap<Color, Map<Ticket, Integer>> tickets) {
        this.tickets = tickets;
    }

    public HashMap<Color, Integer> getStartPositions() {
        return startPositions;
    }

    public void setStartPositions(HashMap<Color, Integer> startPositions) {
        this.startPositions = startPositions;
    }

    @Override
    public String toString() {
        return "SaveData{" +
                "graphName='" + graphName + '\'' +
                ", movesList=" + Arrays.toString(movesList) +
                ", rounds=" + rounds +
                ", ColorList=" + ColorList +
                '}';
    }
}
