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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        TestPost();
        CreatePostReq createPostReq = new CreatePostReq("Test Post", "Test Content", "new URL", "Test Category 1", 1L);
        postService.createPost(createPostReq);
        Post post = entityManager.find(Post.class, 4L);

        assertEquals("Test Post", post.getTitle());
        assertEquals("Test Content", post.getPostBody());
        assertEquals("new URL", post.getImgUrl());
    }

    @Test
    void test_getPostListItems() {
        TestPost();
        List<ListPostsRes> result = postService.getPostListItems();
        Post post = entityManager.find(Post.class, 1L);
        Post post3 = entityManager.find(Post.class, 3L);

        assertTrue(result.get(0).getTitle().equals(post.getTitle()));
        assertTrue(result.get(1).getTitle().equals(post3.getTitle()));
    }

    @Test
    void test_getPostDetailsById() {
        TestPost();
        GetPostRes result = postService.getPostDetailsById(1L);
        Post post = entityManager.find(Post.class, 1L);

        assertTrue(result.getTitle().equals(post.getTitle()));
    }

    @Test
    void test_findPostById() {
        TestPost();
        Post result = postService.findPostById(1L);
        Post post = entityManager.find(Post.class, 1L);

        assertEquals(result, post);
    }

    @Test
    void test_deletePost() {
        TestPost();

        Post post = entityManager.find(Post.class, 1L);
        assertTrue(post.isDeleted() == false);

        postService.deletePost(1L);
        assertTrue(post.isDeleted() == true);
    }

    @Test
    void test_restorationPost() {
        TestPost();

        Post post = entityManager.find(Post.class, 2L);
        assertTrue(post.isDeleted() == true);

        postService.restorationPost(2L);
        assertTrue(post.isDeleted() == false);
    }

    private void TestPost() {
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
