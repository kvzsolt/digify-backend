package hu.progmasters.blog.dto.security;

import hu.progmasters.blog.dto.security.interfaces.PasswordConfirmable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationReq implements PasswordConfirmable {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, message = "Username must be at least 3 characters long")
    @Pattern(regexp = "^\\S*$", message = "Username cannot contain whitespaces")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String password;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String passwordConfirm;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 80, message = "Maximum character limit exceeded (80 character)")
    private String realName;

    @Past(message = "Date must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 500, message = "Maximum character limit exceeded (500 character)")
    private String aboutMe;

    private String profileImageUrl;
}
