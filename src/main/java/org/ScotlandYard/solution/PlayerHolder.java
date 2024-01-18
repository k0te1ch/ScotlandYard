package solution;

import scotlandyard.Colour;
import scotlandyard.Move;
import scotlandyard.Player;
import scotlandyard.Ticket;

import java.util.List;
import java.util.Map;

/**
 * This holds data for a player in one place. Use it instead of separate lists of
 * attributes for all players
 */
public class PlayerHolder {
	private String name;
	private Player player;
	private Colour colour;
	private int currentLocation;
	private int currentVisibleLocation = -1;
	private Map<Ticket, Integer> tickets;

	public PlayerHolder (Player player, Colour colour, int currentLocation, Map<Ticket, Integer> tickets){
		this.player = player;
		this.colour = colour;
		this.currentLocation = currentLocation;
		this.tickets = tickets;

		this.currentLocation = currentLocation;

		// Only update the players visible position if they are not Mr X
		if(Colour.Black == colour) {
			this.currentVisibleLocation = 0;
		} else {
			this.currentVisibleLocation = currentLocation;
		}
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public int getVisiblePosition(){
		return currentVisibleLocation;
	}

	public int getRealPosition(){
		return currentLocation;
	}

	public void updateVisiblePosition(){
		currentVisibleLocation = currentLocation;
	}

	public Move getMove(List<Move> possibleMoves) {
		return player.notify(currentLocation, possibleMoves);
	}

	public Player getPlayer(){ return player;}
	public void setCurrentLocation(final int currentLocation) {
		this.currentLocation = currentLocation;
	}

	public Colour getColour(){
		return colour;
	}

	public Map<Ticket, Integer> getTickets(){
		return tickets;
	}

	public boolean hasEnoughTickets(Ticket ticket){
		return tickets.get(ticket) > 0;
	}

	public boolean hasEnoughTickets(PlayerHolder playerHolder, Ticket ticket1, Ticket ticket2){
		if(ticket1 == ticket2){
			return playerHolder.getTickets().get(ticket1) > 1;
		}else{
			return playerHolder.getTickets().get(ticket1) > 0 && playerHolder.getTickets().get(ticket2) > 0;
		}

	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final PlayerHolder that = (PlayerHolder) o;

		if (colour != that.colour)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return colour != null ? colour.hashCode() : 0;
	}

}
