package hu.progmasters.angularblog.exception;

import lombok.Getter;

@Getter
public class CloudinaryException extends RuntimeException {

    private final String errorMessage;

    public CloudinaryException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
