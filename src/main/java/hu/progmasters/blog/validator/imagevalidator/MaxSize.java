package hu.progmasters.blog.validator.imagevalidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxSizeValidator.class)
public @interface MaxSize {
    String message() default "Image size should not exceed 5 MB";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}