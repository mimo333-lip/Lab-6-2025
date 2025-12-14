package functions;

public class InappropriateFunctionPointException extends Exception {
    public InappropriateFunctionPointException() {
        super();
    }
    
    public InappropriateFunctionPointException(String message) {
        super(message);
    }
    
    public InappropriateFunctionPointException(double x) {
        super("Inappropriate function point with x = " + x);
    }
}