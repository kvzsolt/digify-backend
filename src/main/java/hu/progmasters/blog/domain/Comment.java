package hu.progmasters.blog.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "comment_body", columnDefinition = "TEXT")
    private String commentBody;
    private LocalDateTime createdAt;
    @ManyToOne
    private Account account;
    @ManyToOne
    private Post post;

}
