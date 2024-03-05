package hu.progmasters.blog.validator.imagevalidator;


import hu.progmasters.blog.dto.post.PostImage;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PostImageValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return PostImage.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PostImage postImage = (PostImage) o;
    }
}
