package hu.progmasters.blog.service;

import hu.progmasters.blog.domain.Comment;
import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.dto.comment.ListCommentsRes;
import hu.progmasters.blog.dto.comment.CommentEditReq;
import hu.progmasters.blog.dto.comment.CommentFormReq;
import hu.progmasters.blog.exception.NotFoundCommentException;
import hu.progmasters.blog.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final ModelMapper modelMapper;

    public Comment createComment(CommentFormReq commentFormReq) {

        Post postToComment = postService.findPostById(commentFormReq.getPostId());
        Comment newComment = modelMapper.map(commentFormReq, Comment.class);
        newComment.setPost(postToComment);
        newComment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(newComment);
        return newComment;
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NotFoundCommentException::new);
    }

    public void editComment(Long id, CommentEditReq commentFormData) {
        Comment findedComment = findCommentById(id);
        modelMapper.map(commentFormData, findedComment);
        commentRepository.save(findedComment);
    }

    public void deleteComment(Long id) {
        Comment findedComment = findCommentById(id);
        commentRepository.delete(findedComment);
    }

    public List<ListCommentsRes> getCommentsList(Long id) {
        List<Comment> comments = commentRepository.findAllByPostId(id);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, ListCommentsRes.class))
                .collect(Collectors.toList());
    }
}
