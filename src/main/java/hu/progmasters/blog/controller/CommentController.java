package hu.progmasters.blog.controller;

import hu.progmasters.blog.dto.comment.CommentEditReq;
import hu.progmasters.blog.dto.comment.CommentFormReq;
import hu.progmasters.blog.dto.comment.ListCommentsRes;
import hu.progmasters.blog.service.CommentService;
import hu.progmasters.blog.service.EmailService;
import hu.progmasters.blog.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
@Slf4j
public class CommentController {

    private CommentService commentService;
    private EmailService emailService;
    private PostService postService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity createComment(@Valid @RequestBody CommentFormReq commentFormReq) {
        commentService.createComment(commentFormReq);
        log.info("Http request, POST /api/comments  Comment created");
        emailService.sendEmail(postService.findPostById(commentFormReq.getPostId()).getAccount().getEmail(), "Notification: New Comment on Your Post ID: " + commentFormReq.getPostId(),
                "Someone commented on your post! Post ID: " + commentFormReq.getPostId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and @customPermissionEvaluator.isOwnComment(authentication, #id))")
    @PutMapping("/{commentId}")
    public ResponseEntity editComment(@PathVariable("commentId") Long id,
                                      @Valid @RequestBody CommentEditReq commentFormData) {
        commentService.editComment(id, commentFormData);
        log.info("Http request, PUT /api/comments/{commentId}" + id + " comment edited");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and @customPermissionEvaluator.isOwnComment(authentication, #id))")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long id) {
        commentService.deleteComment(id);
        log.info("Http request, DELETE /api/comments/{commentId}" + id + " comment deleted");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/{postId}")
    public ResponseEntity<List<ListCommentsRes>> getComments(@PathVariable("postId") Long id) {
        log.info("Http request, GET /api/comments/{postId} " + id + " comment list by postid");
        return new ResponseEntity<>(commentService.getCommentsList(id), HttpStatus.OK);
    }
}
