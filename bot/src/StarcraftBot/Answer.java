package StarcraftBot;

import java.util.*;

public abstract class Answer {
	protected String action;
	protected ItemCard actor;
	protected List<ItemCard> itemCards;

	public Answer(String action, ItemCard actor){
		this.action = action;
		this.actor = actor;
	}
	
	public Answer(String action, ItemCard actor, List<ItemCard> itemCards){
		this.action = action;
		this.actor = actor;
		this.itemCards = itemCards;
	}	
	
	public void provideData(String action, ItemCard actor){
		this.action = action;
		this.actor = actor;
	}
	
	public abstract String buildAnswer() throws ItemCardException;
}
