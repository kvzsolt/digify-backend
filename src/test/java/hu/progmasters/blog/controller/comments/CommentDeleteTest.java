package hu.progmasters.blog.controller.comments;

import hu.progmasters.blog.domain.Comment;
import hu.progmasters.blog.exception.comment.NotFoundCommentException;
import hu.progmasters.blog.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.COMMENTS_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CommentDeleteTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CommentService commentService;

    private static final String DELETE_COMMENT_ID_1_MAPPING = COMMENTS_MAPPING + "/1";

    @Test
    void test_commentNotExists() throws Exception {
        createDb();

        mockMvc.perform(delete(DELETE_COMMENT_ID_1_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("COMMENT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No comment found with id")));
    }

    @Test
    void test_DeleteOwnCommentSuccessful() throws Exception {
        createDb();
        Comment comment = entityManager.find(Comment.class, 1L);
        assertNotNull(comment);
        mockMvc.perform(delete(DELETE_COMMENT_ID_1_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isOk());
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(1L));
    }

    @Test
    void test_commentDeleteByAdmin() throws Exception {
        createDb();
        Comment comment = entityManager.find(Comment.class, 1L);
        assertNotNull(comment);
        mockMvc.perform(delete(DELETE_COMMENT_ID_1_MAPPING)
                        .with(user("adminAccount").password("testPassword").roles("ADMIN")))
                .andExpect(status().isOk());
        assertThrows(NotFoundCommentException.class, () -> commentService.findCommentById(1L));
    }

    @Test
    void test_commentDoubleDelete() throws Exception {
        createDb();

        mockMvc.perform(delete(DELETE_COMMENT_ID_1_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isOk());
        mockMvc.perform(delete(DELETE_COMMENT_ID_1_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("COMMENT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No comment found with id")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser', '$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2', 2,'elek@elek.com', true, true);" +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled,account_id,likes) VALUES ('New Post', 'Content','new URL',false,1, false,1,5); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled,account_id,likes) VALUES ('New Post2', 'Content2','new URL2',false,1, false,1,5); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled,account_id,likes) VALUES ('New Post3', 'Content3','new URL3',false, 1, false,1,5); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, category_id, scheduled,account_id,likes) VALUES ('New Post4', 'Content4','new URL4',false,1, false,1,5); " +
                        "INSERT INTO comment (post_Id, account_id, comment_Body) VALUES (1, 1, 'comment');"
        ).executeUpdate();
    }
}
