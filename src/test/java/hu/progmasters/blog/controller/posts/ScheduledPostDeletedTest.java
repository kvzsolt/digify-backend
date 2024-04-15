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
public class ScheduledPostDeletedTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String DELETE_POST_ID_1_SCHEDULED_ENDPOINT = POSTS_MAPPING + "/scheduled/delete/1";
    private static final String DELETE_POST_SCHEDULED_NOT_EXIST = POSTS_MAPPING + "/scheduled/delete/3";
    @Test
    void test_postNotExists() throws Exception {
        createDb();
        mockMvc.perform(put(DELETE_POST_SCHEDULED_NOT_EXIST)
                        .with(user("AuthorUser").password("testPassword").roles("AUTHOR")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    @Test
    void test_ScheduledPostDeleteSuccessful() throws Exception {
        createDb();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put(DELETE_POST_ID_1_SCHEDULED_ENDPOINT)
                        .with(user("AuthorUser").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
    }

    @Test
    void test_ScheduledPostDeleteSuccessfulByAdmin() throws Exception {
        createDb();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put(DELETE_POST_ID_1_SCHEDULED_ENDPOINT)
                        .with(user("AdminUser").password("testPassword").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
    }

    @Test
    void test_ScheduledPostDeleteTriedByUser() throws Exception {
        createDb();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put(DELETE_POST_ID_1_SCHEDULED_ENDPOINT)
                        .with(user("AuthorUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        assertFalse(post.isDeleted());
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AuthorUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AdminUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('UserUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, category_id, account_id, likes) VALUES ('New Post', 'Content','new URL',false, false, 1, 1, 15);"
        ).executeUpdate();
    }
}
