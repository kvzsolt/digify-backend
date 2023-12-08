package hu.progmasters.blog.exception;

import lombok.Getter;

@Getter
public class NoImageException extends RuntimeException {

    private final String errorMessage;
    private final String field;

    public NoImageException(String errorMessage, String field) {
        this.errorMessage = errorMessage;
        this.field = field;
    }
}
