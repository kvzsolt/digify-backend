package hu.progmasters.blog.controller;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class TagsListAllTest {

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
    void test_emptyList() throws Exception {
        mockMvc.perform(get("/api/tag")
                        .with(user("testUser").password("testPassword").roles("GUEST")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void test_tagsListAll() throws Exception {
        TestPost();
        mockMvc.perform(get("/api/tag")
                        .with(user("testUser").password("testPassword").roles("GUEST"))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tagsName", is("New tag")))
                .andExpect(jsonPath("$[1].tagsName", is("New tag2")))
                .andExpect(jsonPath("$[2].tagsName", is("New tag3")))
                .andExpect(jsonPath("$[3].tagsName", is("New tag4")));
    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag2'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag3'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag4'); "
        ).executeUpdate();
    }
}
