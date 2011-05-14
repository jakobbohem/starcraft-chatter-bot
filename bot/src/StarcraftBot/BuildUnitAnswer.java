package StarcraftBot;

import java.util.List;


import StarcraftBot.ItemCard;
import StarcraftBot.ItemCardException;

public class BuildUnitAnswer extends Answer {
	
	public BuildUnitAnswer(String action, ItemCard actor){
		super(action, actor);
	}
	public BuildUnitAnswer(String action, ItemCard actor, List<ItemCard> itemCards){
		super(action, actor, itemCards);
	}
	
	public String buildAnswer() throws ItemCardException{
		try {
			if (super.actor.name==null)
				throw new ItemCardException("name",super.actor);
			if (super.actor.buildsAt==null)
				throw new ItemCardException("buildsAt",super.actor);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = actor.name;
		String buildsAt = actor.buildsAt;
		String answerString = GrammarEngine.nounPlural(name)+" are "+GrammarEngine.verbPastTense(action)+" at the "+buildsAt;
		return answerString;
	}

}
