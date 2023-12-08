package hu.progmasters.blog.dto.post;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListPostsRes {

    private Long id;
    private String title;
    private String postBodyShortened;
    private String imgUrl;
    private String createdAt;
    private String categoryName;
    private List<String> tagsName = new ArrayList<>();
    private Integer numberOfComments;
    private Integer likes;

}
