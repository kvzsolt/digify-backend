package hu.progmasters.blog.exception;

import com.fasterxml.jackson.core.JsonParseException;
import io.jsonwebtoken.JwtException;
import hu.progmasters.blog.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Locale;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;
    private final EmailService emailService;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource, EmailService emailService) {
        this.messageSource = messageSource;
        this.emailService = emailService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidationError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("A validation error occurred: ", ex);
//        emailService.sendEmail("blogprogmasters@gmail.com","Validation error",ex.getMessage());
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        return new ResponseEntity<>(processFieldErrors(fieldErrors), HttpStatus.BAD_REQUEST);
    }

    private ValidationError processFieldErrors(List<FieldError> fieldErrors) {
        ValidationError validationError = new ValidationError();

        for (FieldError fieldError : fieldErrors) {
            validationError.addFieldError(fieldError.getField(), messageSource.getMessage(fieldError, Locale.getDefault()));
        }

        return validationError;
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException ex) {
        logger.error("Request JSON could no be parsed: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError("JSON_PARSE_ERROR","The request could not be parsed as a valid JSON.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("Illegal argument error: ", ex);
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError body = new ApiError("ILLEGAL_ARGUMENT_ERROR", "An illegal argument has been passed to the method.", ex.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> defaultErrorHandler(Throwable t) {
        log.error("An unexpected error occurred: ", t);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError body = new ApiError("UNCLASSIFIED_ERROR", "Oh, snap! Something really unexpected occurred.", t.getLocalizedMessage());

        return new ResponseEntity<>(body, status);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.error("Username not found: ", ex);
        ApiError apiError = new ApiError("USERNAME_NOT_FOUND_ERROR", "The provided username was not found.", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(apiError, status);
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException ex) {
        logger.error("Invalid token: ", ex);
        ApiError apiError = new ApiError("INVALID_TOKEN_ERROR", "The provided token is invalid.", ex.getMessage());
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(NotFoundPostException.class)
    public ResponseEntity<ApiError> handlePostNotFound(Throwable t) {
        log.error("An not found post error occurred: ", t);
        ApiError body = new ApiError("POST_NOT_FOUND_ERROR", "no post found with id", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundPostTagException.class)
    public ResponseEntity<ApiError> handlePostTagNotFound(Throwable t) {
        log.error("An not found postTag error occurred: ", t);
        ApiError body = new ApiError("POST_TAG_NOT_FOUND_ERROR", "no tag found with id", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InsufficientAmountException.class)
    public ResponseEntity<ApiError> handleInsufficientAmount(Throwable t) {
        log.error("An not found postTag error occurred: ", t);
        ApiError body = new ApiError("INSUFFICIENT_FUNDS_ERROR", "kevés a pííínz", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundCommentException.class)
    public ResponseEntity<ApiError> handleCommentNotFound(Throwable t) {
        log.error("An not found comment error occurred: ", t);
        ApiError body = new ApiError("COMMENT_NOT_FOUND_ERROR", "No comment found with id", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundAccountException.class)
    public ResponseEntity<ApiError> handleAccountNotFound(Throwable t) {
        log.error("An not found account error occurred: ", t);
        ApiError body = new ApiError("ACCOUNT_NOT_FOUND_ERROR", "No account found with email", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateEarlierThanTheCurrentOneException.class)
    public ResponseEntity<ApiError> handleDateEarlier(Throwable t) {
        log.error("An earlie date error occurred: ", t);
        ApiError body = new ApiError("EARLY_DATE_ERROR", "The date is earlier than the current one", t.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotExistsException.class)
    public ResponseEntity<List<ValidationError>> handleCategoryNotExists(CategoryNotExistsException exception) {
        log.error("Category not exists: ", exception);
        return new ResponseEntity<>(
//                List.of(new ValidationError()),
                HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(FieldNotAvailableException.class)
    public ResponseEntity<ApiError> handleFieldNotAvailableException(FieldNotAvailableException ex) {
        logger.error("Unavailable field error: ", ex);
        ApiError apiError = new ApiError("UNAVAILABLE_FIELD_ERROR", "An unavailable field error occurred.", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(apiError, status);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiError> handlePasswordsArentMatchingException(PasswordMismatchException ex) {
        logger.error("Passwords are not matching: ", ex);
        ApiError apiError = new ApiError("PASSWORD_MISMATCH_ERROR", "A password mismatch error occurred.", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(apiError, status);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex){
        logger.error("Access denied:", ex);
        ApiError apiError = new ApiError("ACCESS_DENIED", "No appropriate authorisation level", ex.getMessage());
        HttpStatus status = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(apiError, status);
    }

}
