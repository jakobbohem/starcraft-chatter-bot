package StarcraftBot;

public class ItemCardException extends Exception {
    private String msg;
    private String[] problem;
    private Query query;
    
    public ItemCardException(String msg) {
        super(msg);
    }

    public ItemCardException(String msg, String[] problem, Query query) {
        super(msg);
        this.query = query;
        this.problem = problem;
    }
    
    public ItemCardException(String msg, String[] problem){
        this.msg = msg;
        this.problem = problem;
    }
    
    public Query getQuery(){
        return query;
    }
    
    public String[] getProblems() {
        return problem;
    }
    
    public String toString() {
        return msg;
    }
}
