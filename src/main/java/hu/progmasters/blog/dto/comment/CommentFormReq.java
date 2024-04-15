package hu.progmasters.blog.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CommentFormReq {

    @NotNull(message = "Post Id must not be empty")
    private Long postId;
    @NotBlank(message = "Account must not be empty")
    private String account;
    @NotBlank(message = "Comment body must not be empty")
    private String commentBody;

}
