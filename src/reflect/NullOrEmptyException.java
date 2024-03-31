package reflect;

public class NullOrEmptyException extends Exception {

    private static final String message = "attr can't be empty or null, please verify your input.";
    
    public NullOrEmptyException() {
        super(message);
    }

}
