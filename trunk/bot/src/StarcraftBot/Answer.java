package starcraftBot;

import java.util.*;

public abstract class Answer {
	public Answer(String action, ItemCard actor){}
	public Answer(String action, ItemCard actor, List<ItemCard> itemCards){}
	public abstract String buildAnswer() throws ItemCardException;
}
