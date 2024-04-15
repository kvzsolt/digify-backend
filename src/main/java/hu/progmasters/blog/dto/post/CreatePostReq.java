package hu.progmasters.blog.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class CreatePostReq {

    @NotBlank(message = "Title must not be empty")
    private String title;
    @NotBlank(message = "Post body must not be empty")
    private String postBody;
    private String imgUrl;
    private String category;
    private Long accountId;

}
