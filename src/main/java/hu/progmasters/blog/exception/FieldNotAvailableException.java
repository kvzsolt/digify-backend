package hu.progmasters.blog.exception;

public class FieldNotAvailableException extends RuntimeException{
    public FieldNotAvailableException(String message){
        super(message);
    }
}
