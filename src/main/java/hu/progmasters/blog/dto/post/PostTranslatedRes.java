package hu.progmasters.blog.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostTranslatedRes {

    private String title;
    private String postBody;
}
