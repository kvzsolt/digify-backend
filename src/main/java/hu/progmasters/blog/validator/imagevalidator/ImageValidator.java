package hu.progmasters.blog.validator.imagevalidator;

import hu.progmasters.blog.exception.imagevalidator.NoImageException;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ImageValidator implements ConstraintValidator<Image, List<MultipartFile>> {

    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/jpg", "image/png"};
    private final Tika tika = new Tika();

    @Override
    public void initialize(Image constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext context)  {

        if (images != null && !images.isEmpty()) {

            images.forEach(file -> {
                try {
                    String mimeType = tika.detect(file.getInputStream());

                    if (Arrays.stream(ALLOWED_MIME_TYPES).noneMatch(type -> type.equals(mimeType))) {
                        throw new NoImageException("This media type not allowed here: " + mimeType, "Media type");
                    }
                } catch (IOException e) {
                    throw new NoImageException("This media type not allowed here", "Media type");
                }
            });

        }


        return true;
    }

}