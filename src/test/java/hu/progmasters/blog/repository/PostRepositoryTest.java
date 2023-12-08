package hu.progmasters.blog.repository;

import hu.progmasters.blog.domain.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testFindAllPost() {
        Post post = new Post();
        Post post2 = new Post();
        Post post3 = new Post();

        entityManager.persist(post);
        entityManager.persist(post2);

        entityManager.flush();

        List<Post> result = postRepository.findAllByOrderByCreatedAtDesc();

        assertTrue(result.contains(post));
        assertTrue(result.contains(post2));
        assertFalse(result.contains(post3));
    }

    @Test
    public void testFindPostById() {
        Post post = new Post();

        entityManager.persist(post);

        entityManager.flush();

        Post result = postRepository.findPostById(1L);

        assertTrue(result.equals(post));

    }

    @Test
    public void testFindPostByIdWrongId() {
        Post post = new Post();

        entityManager.persist(post);

        entityManager.flush();

        Post result = postRepository.findPostById(2L);

        assertTrue(result == null);

    }
}
