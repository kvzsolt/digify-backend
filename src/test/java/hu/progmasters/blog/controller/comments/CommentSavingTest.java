package hu.progmasters.blog.controller.comments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.COMMENTS_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CommentSavingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;


    @Test
    void test_saveSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
    }

    @Test
    void test_commentBodyIsEmpty() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("commentBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Comment body must not be empty")));
    }

    @Test
    void test_commentBodyNull() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"account\": \"1\" \n" +

                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("commentBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Comment body must not be empty")));
    }

    @Test
    void test_commentAuthorIsEmpty() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"author\": \"\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("account")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Account must not be empty")));
    }

    @Test
    void test_commentAuthorNull() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 1,\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("account")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Account must not be empty")));
    }

    @Test
    void test_commentPostIdNull() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("postId")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Post Id must not be empty")));
    }

    @Test
    void test_postNotExists() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"postId\": 159024,\n" +
                "    \"account\": \"1\",\n" +
                "    \"commentBody\": \"Example comment\" \n" +
                "}";

        mockMvc.perform(post(COMMENTS_MAPPING)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
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
