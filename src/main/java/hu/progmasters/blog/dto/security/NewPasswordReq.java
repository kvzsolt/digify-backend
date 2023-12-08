package hu.progmasters.blog.dto.security;

import hu.progmasters.blog.dto.security.interfaces.PasswordConfirmable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordReq implements PasswordConfirmable {

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String password;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^\\S*$", message = "Password cannot contain whitespaces")
    private String passwordConfirm;
}
