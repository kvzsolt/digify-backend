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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostEditTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String EDIT_POST_ID_1_ENDPOINT = POSTS_MAPPING + "/edit/1";
    private static final String EDIT_POST_NOT_EXIST_ENDPOINT = POSTS_MAPPING + "/edit/3";
    @Test
    void test_editSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"title\": \"edited title\",\n" +
                "    \"postBody\": \"edited post\",\n" +
                "    \"imgUrl\": \"edited url\" \n" +
                "}";
        mockMvc.perform(put(EDIT_POST_ID_1_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
    }

    @Test
    void test_postNotExists() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"title\": \"edited title\",\n" +
                "    \"postBody\": \"edited post\",\n" +
                "    \"imgUrl\": \"edited url\" \n" +
                "}";
        mockMvc.perform(put(EDIT_POST_NOT_EXIST_ENDPOINT)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled,likes, account_id) VALUES ('New Post', 'Content','new URL',1,false,false,15,1); "
        ).executeUpdate();
    }

}
