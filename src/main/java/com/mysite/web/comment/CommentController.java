package com.mysite.web.comment;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.web.post.Post;
import com.mysite.web.post.PostService;
import com.mysite.web.user.SiteUser;
import com.mysite.web.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
	
	private final PostService postService;
	private final CommentService commentService;
	private final UserService userService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable("id") Integer id,@Valid CommentForm commentForm, BindingResult bindingResult) {
        Post post = this.postService.getPost(id);
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "board_detail";
        }
        this.commentService.create(post, commentForm.getContents());
        return String.format("redirect:/post/board/detail/%s", id);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
    	System.out.println("vote!!!!!!!!!!!!!!!!!");
        Comment comment = this.commentService.getComment(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.commentService.vote(comment, siteUser);
        return String.format("redirect:/post/board/detail/%s", comment.getPost().getId());
    }

}
