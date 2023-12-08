package hu.progmasters.blog.controller;


import hu.progmasters.blog.domain.Post;
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
public class PostDeleteTest {

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
                    .roles("AUTHOR")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

    @Test
    void test_deleteOwnPostSuccessful() throws Exception {
        TestPost();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put("/api/posts/delete/1")
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
    }

    @Test
    void test_deleteByAdminSuccessful() throws Exception {
        TestPost();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put("/api/posts/delete/1")
                        .with(user("adminUser").password("testPassword").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
    }

    @Test
    void test_deleteSomeoneElsePost() throws Exception {
        TestPost();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put("/api/posts/delete/1")
                        .with(user("testUserTwo").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
        assertFalse(post.isDeleted());
    }

    @Test
    void test_deletingTheSameTwice() throws Exception {
        TestPost();
        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());
        mockMvc.perform(put("/api/posts/delete/1")
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
        mockMvc.perform(put("/api/posts/delete/1")
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        assertTrue(post.isDeleted());
    }

    @Test
    void test_postNotExists() throws Exception {
        TestPost();
        mockMvc.perform(put("/api/posts/delete/3")
                        .with(user("testUser").password("testPassword").roles("AUTHOR"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no post found with id")));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('testUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('adminUser','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',0,'blog@blog.com',false,false); " +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, likes, account_id) VALUES ('New Post', 'Content','new URL',false,false, 5, 1);" +
                        "INSERT INTO post (title, post_Body, img_Url, deleted, scheduled, likes,account_id) VALUES ('Deleted Post', 'Content','new URL',true,false ,15, 1);"
        ).executeUpdate();
    }
}
