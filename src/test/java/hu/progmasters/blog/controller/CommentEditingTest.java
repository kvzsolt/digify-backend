package hu.progmasters.blog.controller;

import hu.progmasters.blog.domain.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CommentEditingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @TestConfiguration
    public class SecurityTestConfig {

        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("testUser")
                    .password("$2a$10$CLenYEz4sBADpt4CD6tiOeXyn4dWPMFf3KNK/vrtM1u0H8PY5IgPe")
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

    @Test
    void test_commentNotExists() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(put("/api/comments/2")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("COMMENT_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("No comment found with id")));
    }

    @Test
    void test_commentEditSuccessful() throws Exception {
        TestPost();
        Comment comment = entityManager.find(Comment.class, 1L);
        String inputCommand = "{\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";
        assertTrue(comment.getAccount().getUsername().equals("testUser"));
        mockMvc.perform(put("/api/comments/1")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
        assertTrue(comment.getAccount().getUsername().equals("testUser"));
    }

    @Test
    void test_editedCommentBodyIsEmpty() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"\" \n" +
                "}";

        mockMvc.perform(put("/api/comments/1")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("commentBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Comment body must not be empty")));
    }

    @Test
    void test_editedCommentBodyNull() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"1\" \n" +
                "}";

        mockMvc.perform(put("/api/comments/1")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("commentBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Comment body must not be empty")));
    }

    @Test
    void test_editedCommentAuthorIsEmpty() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(put("/api/comments/1")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("account")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Author must not be empty")));
    }

    @Test
    void test_editedCommentAuthorNull() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(put("/api/comments/1")
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("account")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Author must not be empty")));
    }

    private void TestPost() {
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
