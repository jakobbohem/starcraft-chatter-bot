package StarcraftBot;

import java.util.List;


import StarcraftBot.ItemCard;
import StarcraftBot.ItemCardException;

public class BuildUnitAnswer extends Answer {
	
	public BuildUnitAnswer(){
		super();
	}

	public String buildAnswer(String action, ItemCard actor, List<ItemCard> items) throws ItemCardException {
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
		String answerString = String.format("%s are %s at the %s.", 
				GrammarEngine.nounPlural(name), 
				GrammarEngine.verbPastTense(action), 
				buildsAt);
		
		//check for techTree. If there is one, add additional text.
		if (!actor.techTreeList.isEmpty()){
			answerString += "You will also need ";
			if(actor.techTreeList.size() > 2){
				for (int i = 0; i < actor.techTreeList.size()-1; i++) {
					String item = actor.techTreeList.get(i);
					answerString += String.format("%s, ", GrammarEngine.nounIndefinite(item));
				}
				String lastItem = actor.techTreeList.get(actor.techTreeList.size());
				answerString += String.format("and %s",GrammarEngine.nounIndefinite(lastItem));
			}
			else if(actor.techTreeList.size()==1){
				String lastItem = actor.techTreeList.get(actor.techTreeList.size());
				answerString += String.format("%s",GrammarEngine.nounIndefinite(lastItem));
			}
				
		}
			
		return answerString;
	}

}
