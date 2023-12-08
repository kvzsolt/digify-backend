package hu.progmasters.blog.dto.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListPostTagsRes {

    private String tagsName;
    private int numberOfPostsWithSuchTag;
}
