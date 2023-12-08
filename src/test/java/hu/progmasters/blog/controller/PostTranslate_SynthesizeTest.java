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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostTranslate_SynthesizeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Test
    void test_postTranslateSuccessful() throws Exception {
        addUser();
        mockMvc.perform(get("/api/posts/1/translate?target=hu")
                        .with(user("PremiumUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    void test_postTranslateNotAPremiumUser() throws Exception {
        addUser();
        mockMvc.perform(get("/api/posts/1/translate?target=hu")
                        .with(user("NormalUser").password("testPassword").roles("USER"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    //    @Test
//    void test_postSynthesizeSuccessful() throws Exception {
//        String inputCommand = "{\n" +
//                "    \"q\": \"2\",\n" +
//                "    \"target\": \"edited url\" \n" +
//                "}";
//        mockMvc.perform(post("/api/posts/synthesize")
//                        .with(user("testUser").password("testPassword").roles("USER"))
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(inputCommand))
//                .andExpect(status().isOk());
//    }
    private void addUser() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AuthorUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('AdminUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('NormalUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('PremiumUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false, true); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, category_id, account_id, likes) VALUES ('New Post', 'Content','new URL',false, false, 1, 1, 15);"
        ).executeUpdate();
    }
}


 */