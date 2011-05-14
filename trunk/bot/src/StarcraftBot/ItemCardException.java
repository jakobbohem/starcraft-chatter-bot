package StarcraftBot;

public class ItemCardException extends Exception {
	public ItemCardException(String msg, ItemCard card){
		System.err.print(msg);
	}
}
