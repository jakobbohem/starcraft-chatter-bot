package StarcraftBot;

/**
 * A static class which can apply some basic grammar rules to verbs and nouns.
 * @author anton sorensen
 */

public class GrammarEngine {
	
	/**
	 * Returns the simple past-tense form of a verb
	 * @param verb String input
	 * @return The simple past-tense of <code>verb</code>
	 */
	public static String verbPastTense(String verb){
		//query database of irregular verbs for 'verb';
		//if in list, return 'past tense' field
		
		 //Replace this hacky stuff with a proper database call. Would be a lot quicker, too...
		if(verb.equalsIgnoreCase("build"))
			return "built";
		else if(verb.equalsIgnoreCase("make"))
			return "made";
		
		
		char lastChar = verb.toLowerCase().charAt(verb.length()-1);
		
		if (lastChar=='e'){
			return verb+"d";
		}
		else 
			return verb+"ed";
	}
	/**
	 * Returns the plural form of a noun
	 * @param noun String noun input
	 * @return The plural form of <code>noun</code>
	 */
	public static String nounPlural(String noun){
		//query database of irregular nouns for 'noun'
		//if in list, return plural field
		
		char lastChar = noun.toLowerCase().charAt(noun.length()-1);
		
		//If the last part is sibilant, return noun+"es"
		if (lastChar=='s'|| lastChar=='x') {
				return noun+"es";
		}
		//If last character is y, check that the second last character is not a noun. 
		//If so, replace the ending y with ies and return
		else if (lastChar=='y'){
			char secondLastChar = noun.toLowerCase().charAt(noun.length()-2);
			if (!isVowel(secondLastChar)){
				return noun.toLowerCase().substring(0,noun.length()-1)+"ies";
			} else
				return noun+"s";
		} else
			return noun+"s";
	}
	
	/**
	 * Puts an "a" or an "an" before a noun, depending on whether or not the first letter is a vowel.
	 * @param noun The noun
	 * @return A string consisting of "a <code>noun</code>" or "an <code>noun</code>".
	 */
	public static String nounIndefinite(String noun){
		if (isVowel(noun.toLowerCase().charAt(0)))
			return "an "+noun;
		else
			return "a "+noun;
	}
	
	/**
	 * Checks whether or not a letter is a vowel. 
	 */
	private static boolean isVowel(char a){
		if (a=='a' || a=='e'|| a=='i' || a=='o' || a=='u' || a=='y')
			return true;
		else
			return false;
	}
	/*
	private capitalizeSentences(String input){
		Regex r = new Regex("[.]([a-z])","[.]${1}");
		return r.replaceAll(input);
	}
	*/
}
