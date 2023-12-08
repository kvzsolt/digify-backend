package hu.progmasters.blog.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(name = "post_body", columnDefinition = "TEXT")
    private String postBody;
    private String imgUrl;
    private LocalDateTime createdAt;
    private boolean deleted;
    private boolean scheduled;
    private int likes;
    @ManyToOne
    private PostCategory category;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    @OrderBy(value = "createdAt desc")
    private List<Comment> comments;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();
    @ManyToMany(mappedBy = "posts")
    private List<PostTag> postTags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

//    Későbbi fejlesztés
//    @ManyToMany(mappedBy = "favoritePosts")
//    private Set<Account> favoritedBy;
}
