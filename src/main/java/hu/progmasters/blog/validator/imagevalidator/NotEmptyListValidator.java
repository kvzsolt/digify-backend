package hu.progmasters.blog.validator.imagevalidator;

import hu.progmasters.blog.exception.imagevalidator.FilesMissingException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, List<MultipartFile>> {

    @Override
    public void initialize(NotEmptyList constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {

        boolean isValid = files != null && !files.isEmpty();

        if (!isValid) {
            throw new FilesMissingException("Image upload required", "imageList");
        }

        return isValid;
    }
}
