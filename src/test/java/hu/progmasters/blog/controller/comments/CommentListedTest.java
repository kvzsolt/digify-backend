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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CommentListedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String COMMENT_ID_1_ENDPOINT = COMMENTS_MAPPING + "/1";

    @Test
    void test_atStart_emptyList() throws Exception {
        mockMvc.perform(get(COMMENT_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void test_postCommentListAll() throws Exception {
        createDb();
        System.out.println();
        mockMvc.perform(get(COMMENT_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentBody", is("comment")))
                .andExpect(jsonPath("$[1].commentBody", is("comment2")))
                .andExpect(jsonPath("$[2].commentBody", is("comment3")))
                .andExpect(jsonPath("$[3].commentBody", is("comment4")));
    }

    private void createDb() {
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
