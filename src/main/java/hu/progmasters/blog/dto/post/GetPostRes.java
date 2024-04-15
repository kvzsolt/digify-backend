package hu.progmasters.blog.dto.post;

import hu.progmasters.blog.dto.comment.ListCommentsRes;
import hu.progmasters.blog.dto.tag.TagDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPostRes {

    private Long id;
    private String title;
    private String postBody;
    private String imgUrl;
    private String createdAt;
    private String categoryName;
    private List<TagDetails> postTags;
    private List<ListCommentsRes> comments;
    private int likes;
    private List<String> imageUrls;

}
