package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.domain.PostTag;
import hu.progmasters.blog.dto.tag.ListPostTagsRes;
import hu.progmasters.blog.dto.tag.TagCreationReq;
import hu.progmasters.blog.exception.posts.NotFoundPostTagException;
import hu.progmasters.blog.repository.TagsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TagService {

    private TagsRepository tagsRepository;
    private PostService postService;
    private ModelMapper modelMapper;

    public void createTags(Long id, TagCreationReq req) {
        Post post = postService.findPostById(id);

            PostTag checkedTag = tagsRepository.findByTagByNameByPostId(req.getTagsName(), id).orElse(null);
            if (checkedTag == null) {
                PostTag tag = modelMapper.map(req, PostTag.class);
                tagsRepository.save(tag);
                tag.getPosts().add(post);
                post.getPostTags().add(tag);
        }
    }

    public void deleteTags(Long id) {
        PostTag tag = findPostTagById(id);
        tagsRepository.delete(tag);
    }

    public void editingTag(Long id, TagCreationReq tagCreationReq) {
        PostTag editedTag = findPostTagById(id);
        modelMapper.map(tagCreationReq, editedTag);
        tagsRepository.save(editedTag);
    }

    public PostTag findPostTagById(Long id) {
        return tagsRepository.findById(id).orElseThrow(NotFoundPostTagException::new);
    }

    public List<ListPostTagsRes> getPostTagListItems() {
        return tagsRepository.findAllOrderBy();
    }
}
