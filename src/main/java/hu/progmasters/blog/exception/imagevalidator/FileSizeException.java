package hu.progmasters.blog.exception.imagevalidator;

import lombok.Getter;

@Getter
public class FileSizeException extends RuntimeException {

    private final String errorMessage;
    private final String size;

    public FileSizeException(String errorMessage, String size) {
        this.errorMessage = errorMessage;
        this.size = size;
    }
}
