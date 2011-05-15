package StarcraftBot;

public class ItemCardException extends Exception {
   public ItemCardException(String message){
       super(message);   }

	public ItemCardException(String msg, ItemCard card){
		System.err.print(msg);
	}
}
