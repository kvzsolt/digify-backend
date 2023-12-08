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
//
//        if (postImage.getTitle() == null || postImage.getTitle().isEmpty()) {
//            errors.rejectValue("title", "postFormData.title.empty");
//        }
//        if (postImage.getPostBody() == null || postImage.getPostBody().isEmpty()) {
//            errors.rejectValue("postBody", "postFormData.postBody.empty");
//        }
    }
}
