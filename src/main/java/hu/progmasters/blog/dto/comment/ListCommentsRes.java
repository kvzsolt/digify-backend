package hu.progmasters.blog.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListCommentsRes {

    private String account;
    private String commentBody;
    private LocalDateTime createdAt;
}
