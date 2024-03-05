package hu.progmasters.blog.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Getter
public class ValidationError {

    @Singular
    private final List<CustomFieldError> fieldErrors;

    @Builder
    private ValidationError(List<CustomFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public void addFieldError(String field, String message) {
        fieldErrors.add(new CustomFieldError(field, message));
    }

    @Getter
    @Builder
    static class CustomFieldError {
        private final String field;
        private final String message;
    }
}
