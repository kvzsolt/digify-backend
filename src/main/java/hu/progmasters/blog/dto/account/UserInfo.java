package hu.progmasters.blog.dto.account;


import hu.progmasters.blog.dto.post.GetPostsShortenedRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String username;
    private String email;
    private boolean newsletter;
    private boolean premium;
    private List<GetPostsShortenedRes> writtenPosts;
    private String realName;
    private LocalDate dateOfBirth;
    private String aboutMe;
    private String profileImageUrl;

}
