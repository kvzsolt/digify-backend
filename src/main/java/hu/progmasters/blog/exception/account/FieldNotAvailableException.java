package hu.progmasters.blog.exception.account;

public class FieldNotAvailableException extends RuntimeException{
    public FieldNotAvailableException(String message){
        super(message);
    }
}
