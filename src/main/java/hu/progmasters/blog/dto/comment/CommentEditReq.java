package hu.progmasters.blog.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CommentEditReq {

    @NotBlank(message = "Author must not be empty")
    private String account;
    @NotBlank(message = "Comment body must not be empty")
    private String commentBody;
}
