package hu.progmasters.blog.controller.posts;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostGetIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String GET_POST_ID_1_ENDPOINT = POSTS_MAPPING + "/1";
    private static final String GET_POST_NOT_EXIST = POSTS_MAPPING + "/2";


    @Test
    void test_postNotExists() throws Exception {
        createDb();
        mockMvc.perform(get(GET_POST_NOT_EXIST)
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    @Test
    void test_postGetById() throws Exception {
        createDb();
        mockMvc.perform(get(GET_POST_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Post")))
                .andExpect(jsonPath("$.postBody", is("Content")))
                .andExpect(jsonPath("$.imgUrl", is("new URL")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('Test Kata','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes, account_id) VALUES ('New Post', 'Content','new URL',1 , false, false, 15, 1);"
        ).executeUpdate();
    }
}
