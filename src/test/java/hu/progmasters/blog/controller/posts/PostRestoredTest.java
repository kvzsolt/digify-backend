package hu.progmasters.blog.controller.posts;

import hu.progmasters.blog.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static hu.progmasters.blog.controller.constants.Endpoints.POSTS_MAPPING;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostRestoredTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String RESTORE_POST_ID_2_ENDPOINT = POSTS_MAPPING + "/restore/2";
    private static final String RESTORE_POST_NOT_EXIST_ENDPOINT = POSTS_MAPPING + "/restore/3";


    @Test
    void test_restoreADeletedPost() throws Exception {
        createDb();
        Post post = entityManager.find(Post.class, 2L);
        assertTrue(post.isDeleted());
        mockMvc.perform(put(RESTORE_POST_ID_2_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertFalse(post.isDeleted());
    }

    @Test
    void test_postNotExists() throws Exception {
        createDb();
        mockMvc.perform(put(RESTORE_POST_NOT_EXIST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, account_id, category_id, likes) VALUES ('New Post', 'Content','new URL',false,false, 1, 1 ,15);" +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, account_id, category_id, likes) VALUES ('Deleted Post', 'Content','new URL',true,false, 1, 1, 15);"
        ).executeUpdate();
    }
}
