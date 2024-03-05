package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.dto.tag.ListPostTagsRes;
import hu.progmasters.blog.dto.tag.TagCreationReq;
import hu.progmasters.blog.dto.tag.TagDetails;
import hu.progmasters.blog.exception.posts.NotFoundPostTagException;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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
public class TagServiceTest {
    @Autowired
    private TagService tagService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    void test_createTags() {
        TestPost();
        TagCreationReq tagsFromData = new TagCreationReq("Test tag");
        tagService.createTags(1L, tagsFromData);
        PostTag postTag = tagService.findPostTagById(4L);

        assertEquals("Test tag", postTag.getTagsName());
    }

    @Test
    void test_deleteTags() {
        TestPost();
        assertTrue(tagService.findPostTagById(1L) != null);
        tagService.deleteTags(1L);
        assertThrows(NotFoundPostTagException.class, () -> tagService.findPostTagById(1L));
    }

    @Test
    void test_tagsDetails() {
        TestPost();
        PostTag postTag = tagService.findPostTagById(1L);
        TagDetails tagDetails = modelMapper.map(postTag, TagDetails.class);
        assertTrue(postTag.getTagsName().equals(tagDetails.getTagsName()));
    }

    @Test
    void test_editComment() {
        TestPost();
        PostTag postTag = tagService.findPostTagById(1L);
        assertEquals("New tag", postTag.getTagsName());

        TagCreationReq tagCreationReq = new TagCreationReq("Test tag");
        tagService.editingTag(1L, tagCreationReq);

        assertEquals("Test tag", postTag.getTagsName());
    }

    @Test
    void test_findPostTagById() {
        TestPost();
        PostTag postTag = tagService.findPostTagById(1L);
        assertEquals("New tag", postTag.getTagsName());
    }

    @Test
    void test_getPostTagListItems() {
        TestPost();
        List<ListPostTagsRes> result = tagService.getPostTagListItems();
        PostTag postTag = entityManager.find(PostTag.class, 1L);
        PostTag postTag2 = entityManager.find(PostTag.class, 2L);
        PostTag postTag3 = entityManager.find(PostTag.class, 3L);

        assertTrue(result.contains(modelMapper.map(postTag, ListPostTagsRes.class)));
        assertTrue(result.contains(modelMapper.map(postTag2, ListPostTagsRes.class)));
        assertTrue(result.contains(modelMapper.map(postTag3, ListPostTagsRes.class)));

    }

    private void TestPost() {
        entityManager.createNativeQuery(
                "INSERT INTO category (category_name) VALUES ('Test Category 1'); " +
                        "INSERT INTO post (title, post_Body, img_Url, category_id, deleted, scheduled, likes) VALUES ('New Post', 'Content','new URL',1,false,false,0); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag2'); " +
                        "INSERT INTO post_tag ( tags_name ) VALUES ('New tag3'); "
        ).executeUpdate();
    }
}
