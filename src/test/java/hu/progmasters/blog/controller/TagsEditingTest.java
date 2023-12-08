package hu.progmasters.blog.controller;

import hu.progmasters.blog.domain.PostTag;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsEditingTest {

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
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }
    }

    @Test
    void test_tagsNotExists() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"Edited Tag\" \n" +
                "}";
        mockMvc.perform(put("/api/tag/3")
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("POST_TAG_NOT_FOUND_ERROR")))
                .andExpect(jsonPath("$.error", is("no tag found with id")));
    }

    @Test
    void test_tagEditSuccessful() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"Edited Tag\" \n" +
                "}";
        mockMvc.perform(put("/api/tag/1")
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isOk());
        PostTag updatedTag = entityManager.find(PostTag.class, 1L);
        assertEquals("Edited Tag", updatedTag.getTagsName());
    }

    @Test
    void test_tagsNameIsEmpty() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "    \"tagsName\": \"\" \n" +
                "}";
        mockMvc.perform(put("/api/tag/1")
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    @Test
    void test_tagsNameIsNull() throws Exception {
        TestPost();
        String inputCommand = "{\n" +
                "}";
        mockMvc.perform(put("/api/tag/1")
                        .with(user("testUser").password("testPassword").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(inputCommand))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("tagsName")))
                .andExpect(jsonPath("$.fieldErrors[0].message", is("Tag cannot be empty")));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); "
        ).executeUpdate();
    }
}
