package calendar;

public class IllegalDateException extends RuntimeException {
    
    public IllegalDateException(String errorMessage) {
        super(errorMessage);
    }

}