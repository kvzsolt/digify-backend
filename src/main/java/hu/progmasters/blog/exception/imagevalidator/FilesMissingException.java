package hu.progmasters.blog.exception.imagevalidator;

import lombok.Getter;

@Getter
public class FilesMissingException extends RuntimeException{

    private final String errorMessage;
    private final String field;

    public FilesMissingException(String errorMessage, String field) {
        this.errorMessage = errorMessage;
        this.field = field;
    }
}
