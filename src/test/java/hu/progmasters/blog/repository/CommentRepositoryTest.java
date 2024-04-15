package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager entityManager;




    @BeforeEach
    public void setup() {
        createDb();
        entityManager.flush();
    }

    @Test
    public void test_findAllCommentByPostId() {
        Comment comment = entityManager.find(Comment.class, 1L);
        Comment comment2 = entityManager.find(Comment.class, 2L);
        Comment comment3 = entityManager.find(Comment.class, 3L);

        List<Comment> result = commentRepository.findAllByPostId(1L);

        assertTrue(result.contains(comment));
        assertTrue(result.contains(comment2));
        assertTrue(result.contains(comment3));
    }

    @Test
    public void test_findAllCommentByPostIdOtherPostCommentAreNotDisplayed() {

        List<Comment> result = commentRepository.findAllByPostId(1L);

        Comment comment = entityManager.find(Comment.class, 1L);
        Comment comment2 = entityManager.find(Comment.class, 2L);
        Comment comment3 = entityManager.find(Comment.class, 3L);
        Comment comment4 = entityManager.find(Comment.class, 4L);
        assertTrue(result.contains(comment));
        assertTrue(result.contains(comment2));
        assertTrue(result.contains(comment3));

        assertFalse(result.contains(comment4));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post', 'Content','new URL',1 , false, false, 0); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post2', 'Content2','new URL2',1 , false, false, 0); " +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1, 'comment');" +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1, 'comment2');" +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (1, 'comment3');" +
                        "INSERT INTO comment (post_Id, comment_Body) VALUES (2, 'comment4');"
        ).executeUpdate();
    }
}
