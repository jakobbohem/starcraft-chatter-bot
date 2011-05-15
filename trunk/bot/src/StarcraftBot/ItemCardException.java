package StarcraftBot;

public class ItemCardException extends Exception {
    private String msg;
    private ItemCard card;
    
    public ItemCardException(String msg) {
        super(msg);
    }

    public ItemCardException(String msg, ItemCard card) {
        super(msg);
        this.card = card;
    }
}
