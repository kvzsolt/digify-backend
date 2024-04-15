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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ScheduledPostSavingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    private static final String SAVE_SCHEDULED_POST = POSTS_MAPPING + "/scheduled/23,59";
    private static final String SAVE_SCHEDULED_POST_EARLY_DATE = POSTS_MAPPING + "/scheduled/0,1";

    @Test
    void test_postBodyIsEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"postBody\": \"\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("postBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Post body must not be empty")));
    }

    @Test
    void test_postBodyIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("postBody")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Post body must not be empty")));
    }

    @Test
    void test_titleIsEmpty() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"\",\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Title must not be empty")));
    }

    @Test
    void test_titleIsNull() throws Exception {
        String inputCommand = "{\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST)
                        .with(user("testUser").password("testPassword").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("title")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Title must not be empty")));
    }

    @Test
    void test_earlyDate() throws Exception {
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\" \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST_EARLY_DATE)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("EARLY_DATE_ERROR")))
                .andExpect(jsonPath("$.error", is("The date is earlier than the current one")));
    }

    @Test
    void test_saveSuccessful() throws Exception {
        createDb();
        String inputCommand = "{\n" +
                "    \"title\": \"New title\",\n" +
                "    \"postBody\": \"Example post\",\n" +
                "    \"imgUrl\": \"Example url\", \n" +
                "    \"category\": \"Test Category 1\", \n" +
                "     \"accountId\": 1 \n" +
                "}";

        mockMvc.perform(post(SAVE_SCHEDULED_POST)
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isCreated());
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
