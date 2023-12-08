
package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Comment;
import hu.progmasters.blog.dto.comment.CommentEditReq;
import hu.progmasters.blog.dto.comment.CommentFormReq;
import hu.progmasters.blog.dto.comment.ListCommentsRes;
import hu.progmasters.blog.exception.NotFoundCommentException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ModelMapper modelMapper;
    @Test
    void test_createComment() {
        TestPost();
        CommentFormReq commentFormReq = new CommentFormReq(1l,"Lajos","test comment");
        commentService.createComment(commentFormReq);
        Comment comment = commentService.findCommentById(5L);

        assertEquals("test comment", comment.getCommentBody());
    }
    @Test
    void test_findCommentById(){
        TestPost();
        Comment comment = commentService.findCommentById(1L);
        assertEquals("2023-11-26T00:00", comment.getCreatedAt().toString());
        assertEquals("comment", comment.getCommentBody());
    }
    @Test
    void test_editComment() {
        TestPost();
        Comment comment = commentService.findCommentById(1L);
        assertEquals("comment", comment.getCommentBody());

        CommentEditReq commentFormData = new CommentEditReq("edited author","edited commentBody");
        commentService.editComment(1L,commentFormData);

        assertEquals("edited commentBody", comment.getCommentBody());
    }

    @Test
    void test_deleteComment() {
        TestPost();
        Comment comment = commentService.findCommentById(1L);
        assertTrue(comment.getCommentBody().equals("comment"));
        commentService.deleteComment(1L);
        assertThrows(NotFoundCommentException.class,()->commentService.findCommentById(1L) );
    }

    @Test
    void test_getCommentsList(){
        TestPost();
        List<ListCommentsRes> result = commentService.getCommentsList(1L);
        Comment comment = entityManager.find(Comment.class,1L);
        Comment comment2 = entityManager.find(Comment.class,2L);
        Comment comment3 = entityManager.find(Comment.class,3L);
        assertTrue(result.contains(modelMapper.map(comment,ListCommentsRes.class)));
        assertTrue(result.contains(modelMapper.map(comment2,ListCommentsRes.class)));
        assertTrue(result.contains(modelMapper.map(comment3,ListCommentsRes.class)));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser', '$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2', 2,'elek@elek.com', true, true);" +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled,account_id,likes) VALUES ('New Post', 'Content','new URL',false,1, false,1,5); " +
                        "INSERT INTO comment (post_Id, comment_Body, created_at) VALUES (1, 'comment', '2023-11-26');" +
                        "INSERT INTO comment (post_Id, comment_Body, created_at) VALUES (1, 'comment2', '2023-11-26');" +
                        "INSERT INTO comment (post_Id, comment_Body, created_at) VALUES (1, 'comment3', '2023-11-26');" +
                        "INSERT INTO comment (post_Id, comment_Body, created_at) VALUES (1, 'comment4', '2023-11-26');"

        ).executeUpdate();
    }



}
