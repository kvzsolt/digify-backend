package hu.progmasters.blog.domain;

import hu.progmasters.blog.domain.enums.AccountRole;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private AccountRole role;

    @Column(name = "newsletter")
    private boolean newsletter;

    @Column(name = "premium")
    private boolean premium;

    @OneToMany(mappedBy = "account")
    private List<Post> writtenPosts;

    @OneToMany(mappedBy = "account")
    private List<PayOrder> payOrder;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "profile_image")
    private String profileImageUrl;

    @OneToMany(mappedBy = "account")
    private List<Comment> writtenComments;


}
