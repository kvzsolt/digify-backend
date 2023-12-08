/*
 * Copyright © Progmasters (QTC Kft.), 2018.
 * All rights reserved. No part or the whole of this Teaching Material (TM) may be reproduced, copied, distributed,
 * publicly performed, disseminated to the public, adapted or transmitted in any form or by any means, including
 * photocopying, recording, or other electronic or mechanical methods, without the prior written permission of QTC Kft.
 * This TM may only be used for the purposes of teaching exclusively by QTC Kft. and studying exclusively by QTC Kft.’s
 * students and for no other purposes by any parties other than QTC Kft.
 * This TM shall be kept confidential and shall not be made public or made available or disclosed to any unauthorized person.
 * Any dispute or claim arising out of the breach of these provisions shall be governed by and construed in accordance with the laws of Hungary.
 */

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
