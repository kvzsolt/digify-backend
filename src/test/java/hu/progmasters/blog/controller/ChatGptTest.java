package hu.progmasters.blog.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ChatGptTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

//    @Test
//    void test_chatGptChatSuccessful() throws Exception {
//        addUser();
//        String inputCommand = "{\n" +
//                "    \"message\": \"Hello gpt\" \n" +
//                "}";
//
//        mockMvc.perform(post("/api/gpt/chat")
//                        .with(user("tesztElek").password("$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void test_chatGptGeneratePictureSuccessful() throws Exception {
//        addUser();
//        String inputCommand = "{\n" +
//                "    \"prompt\": \"Big city\" \n" +
//                "}";
//        mockMvc.perform(post("/api/public/gpt/generate")
//                        .with(user("tesztElek").password("$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void test_chatGptGeneratePictureByTitleSuccessful() throws Exception {
//        addUser();
//        TestPost();
//        mockMvc.perform(post("/api/gpt/generate/1")
//                        .with(user("tesztElek").password("$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void test_chatGptGeneratePictureByTitlePostNotExist() throws Exception {
//        addUser();
//        String inputCommand = "{\n" +
//                "    \"message\": \"Hello gpt\" \n" +
//                "}";
//        mockMvc.perform(post("/api/gpt/generate/2")
//                        .with(user("tesztElek").password("$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
//                .andExpect(jsonPath("$.error", is("no post found with id")));
//    }
//
//    private void TestPost() {
//        entityManager.createNativeQuery(
//                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('Test Kata','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
//                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
//                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, account_id, likes) VALUES ('New Post', 'Content','new URL',1,false,false,1,0); "
//        ).executeUpdate();
//    }
//
//    private void addUser() {
//        entityManager.createNativeQuery(
//                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('tesztElek', '$2a$10$RVP8Q2ybES8dJVGbJP54xehMmrzetBxHJZzNNhvBvRmv2gl7bkNt2', 2,'elek@elek.com', true, true);"
//        ).executeUpdate();
//    }
}
