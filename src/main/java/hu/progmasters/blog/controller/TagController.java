package hu.progmasters.blog.controller;

import hu.progmasters.blog.dto.tag.ListPostTagsRes;
import hu.progmasters.blog.dto.tag.TagCreationReq;
import hu.progmasters.blog.service.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static hu.progmasters.blog.controller.constants.Endpoints.TAG_MAPPING;

@RestController
@RequestMapping(TAG_MAPPING)
@AllArgsConstructor
@Slf4j
public class TagController {

    private TagService tagService;

    @PreAuthorize("hasRole('ROLE_AUTHOR')")
    @PostMapping("/{postId}")
    public ResponseEntity createTags(@PathVariable("postId") Long id,
                                     @Valid @RequestBody TagCreationReq req) {
        tagService.createTags(id, req);
        log.info("Http request, POST /api/tags/postId: " + id + " Tags created");
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{PostTagId}")
    public ResponseEntity<Void> deletePostTag(@PathVariable("PostTagId") Long id) {
        tagService.deleteTags(id);
        log.info("Http request, PUT /api/tags/{postTagId}" + id + " tags deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{postTagId}")
    public ResponseEntity editPostTag(@PathVariable("postTagId") Long id,
                                      @Valid @RequestBody TagCreationReq tagCreationReq) {
        tagService.editingTag(id, tagCreationReq);
        log.info("Http request, PUT /api/tags/{postTagId}" + id + " Tag edited");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping
    public ResponseEntity<List<ListPostTagsRes>> getPostTagList() {
        log.info("Http request, GET /api/tags/  All tags listed");
        return new ResponseEntity<>(tagService.getPostTagListItems(), HttpStatus.OK);
    }
}
