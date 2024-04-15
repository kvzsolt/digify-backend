package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.dto.post.CreatePostReq;
import hu.progmasters.blog.dto.post.GetPostRes;
import hu.progmasters.blog.dto.post.ListPostsRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private EntityManager entityManager;

    @Test
    void test_createPost() {
        createDb();
        CreatePostReq createPostReq = new CreatePostReq("Test Post", "Test Content", "new URL", "Test Category 1", 1L);
        postService.createPost(createPostReq);
        Post post = entityManager.find(Post.class, 4L);

        assertEquals("Test Post", post.getTitle());
        assertEquals("Test Content", post.getPostBody());
        assertEquals("new URL", post.getImgUrl());
    }

    @Test
    void test_getPostListItems() {
        createDb();
        List<ListPostsRes> result = postService.getPostListItems();
        Post post = entityManager.find(Post.class, 1L);
        Post post3 = entityManager.find(Post.class, 3L);

        assertEquals(result.get(0).getTitle(), post.getTitle());
        assertEquals(result.get(1).getTitle(), post3.getTitle());
    }

    @Test
    void test_getPostDetailsById() {
        createDb();
        GetPostRes result = postService.getPostDetailsById(1L);
        Post post = entityManager.find(Post.class, 1L);

        assertEquals(result.getTitle(), post.getTitle());
    }

    @Test
    void test_findPostById() {
        createDb();
        Post result = postService.findPostById(1L);
        Post post = entityManager.find(Post.class, 1L);

        assertEquals(result, post);
    }

    @Test
    void test_deletePost() {
        createDb();

        Post post = entityManager.find(Post.class, 1L);
        assertFalse(post.isDeleted());

        postService.deletePost(1L);
        assertTrue(post.isDeleted());
    }

    @Test
    void test_restorationPost() {
        createDb();

        Post post = entityManager.find(Post.class, 2L);
        assertTrue(post.isDeleted());

        postService.restorationPost(2L);
        assertFalse(post.isDeleted());
    }

    private void createDb() {
        entityManager.createNativeQuery(
                "INSERT INTO account (username,password,role,email,newsletter,premium) VALUES ('Test Kata','$2a$10$yyKaHq8PYGDVVLeim1l6vOuibvBUiIzok/HjB3BJxTnu36EXRngau',2,'blog@blog.com',false,false); " +
                        "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post', 'Content','new URL',1,false,false,0); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post2', 'Content','new URL',1,true,false,0); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post3', 'Content','new URL',1,false,false,0); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); "
        ).executeUpdate();
    }
}
