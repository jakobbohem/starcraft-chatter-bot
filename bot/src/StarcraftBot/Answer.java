package StarcraftBot;

import java.util.*;

public abstract class Answer {
	protected String action;
	protected ItemCard actor;
	protected List<ItemCard> items;

	public Answer(){
	}
	
	public abstract String buildAnswer(String action, ItemCard actor, List<ItemCard> items) throws ItemCardException;
}
