package com.mysite.web.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@RequestMapping("/post")
@Controller
@RequiredArgsConstructor 
public class PostController {
	
	private final PostService postService;
	
	@GetMapping("/board")
	public String list(Model model,@RequestParam(value="page",defaultValue="0") int page) {
		Page<Post> paging = this.postService.getList(page);
        model.addAttribute("paging", paging);
        return "board";
    }
	
	@GetMapping(value = "/board/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id) {
		
		Post post = this.postService.getPost(id);
        model.addAttribute("post", post);
        
        return "board_detail";
    }
	
	 @GetMapping("/create")
	    public String postCreate(PostForm postForm) {
	        return "post_form";
	    }
	 
	 @PostMapping("/create")
	    public String questionCreate(@Valid PostForm postForm, BindingResult bindingResult) {
	        if (bindingResult.hasErrors()) {
	            return "post_form";
	        }
	        this.postService.create(postForm.getTitle(), postForm.getContents());
	        return "redirect:/post/board";
	    }

}
