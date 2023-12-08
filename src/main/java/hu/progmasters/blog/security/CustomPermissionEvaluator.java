package hu.progmasters.blog.security;

import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.domain.Comment;
import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.service.AccountService;
import hu.progmasters.blog.service.CommentService;
import hu.progmasters.blog.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@AllArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private PostService postService;
    private CommentService commentService;
    private AccountService accountService;

     public boolean isOwnPost(Authentication auth, Long id) {
        if (auth == null || id == null) {
            return false;
        }

        Post post = postService.findPostById(id);
        if(!post.getAccount().getUsername().equals(auth.getName())){
            throw new AccessDeniedException("Access denied.");
         }
        return true;
    }
    public boolean isOwnComment(Authentication auth, Long id){
        if (auth == null || id == null) {
            return false;
        }
        Comment comment = commentService.findCommentById(id);
        if(!comment.getAccount().getUsername().equals(auth.getName())){
            throw new AccessDeniedException("Access denied.");
        }
        return true;

    }
    public boolean isOwnAccount(Authentication auth, String email){
        if(auth == null || email == null) {
            return false;
        }
        Account accountToReset = accountService.findByEmail(email);
        if(!accountToReset.getUsername().equals(auth.getName())){
            throw new AccessDeniedException("Access denied.");
        }
        return true;
    }
    public boolean isOwnAccountById(Authentication auth, Long id){
        if(auth == null || id == null){
            return false;
        }
        Account accountToUpdate = accountService.findAccountById(id);
        if(!accountToUpdate.getUsername().equals(auth.getName())){
            throw new AccessDeniedException("Access denied.");
        }
        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object o, Object o1) {
        return false;
    }
}