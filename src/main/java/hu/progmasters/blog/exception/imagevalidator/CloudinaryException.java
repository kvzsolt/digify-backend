package hu.progmasters.blog.exception.imagevalidator;

import lombok.Getter;

@Getter
public class CloudinaryException extends RuntimeException {

    private final String errorMessage;

    public CloudinaryException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
