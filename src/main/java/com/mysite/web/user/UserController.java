package com.mysite.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.web.post.Post;
import com.mysite.web.post.PostForm;
import com.mysite.web.post.PostService;

import java.util.List;
import java.util.Optional;
import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private final PostService postService;
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("/user/signup")
	public String SignupForm(UserCreateForm usercreateform) {
        return "signup_form";
    }
	
	@PostMapping("/user/signup")
    public String questionCreate(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        
		if (bindingResult.hasErrors()) {
            return "signup_form";
        }
        
        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2()))
        {
        	bindingResult.rejectValue("password2", "passwordInCorrect", 
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        
        //회원가입 성공
        try {
        this.userService.create(userCreateForm.getUsername(), userCreateForm.getPassword1(),
        		userCreateForm.getName());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
       
        return "redirect:/";
    }
	
	
	@GetMapping("/user/login")
    public String login() {
        return "login_form";
    }
	 
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/user/info")
    public String userInfo(Principal principal, Model model,UserModifyForm userModifyForm) {
        

		SiteUser user =this.userService.getUser(principal.getName());
        

        model.addAttribute("user", user);
        return "info";
    }
	
	
	
	
	@PostMapping("/user/modifyName")
    public String nameModify(UserModifyForm userModifyForm,  Model model,
            Principal principal) {
        
        
        SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
        this.userService.modifyName(user, userModifyForm.getName());
        return "info";
    }
	 
	@GetMapping("/user/password_check")
    public String password_check(UserModifyForm userModifyForm) {

        
        return "password_check";
    }
	 
	@PostMapping("/user/modifyPassword")
    public String passwordModify(UserModifyForm userModifyForm,  Model model,
            Principal principal) {
        
        SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
        
        if(passwordEncoder.matches(userModifyForm.getPassword1(),user.getPassword()))
        {
        	this.userService.modifyPassword(user,userModifyForm.getPassword2() );
        	return "info";
        	
        }
        else
        {
        	
            return "password_check";
        }
        
        
    }
	
	@GetMapping("/user/userPost")
	public String userPost(Model model,Principal principal)
	{
		SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
		
		return "user_postlist";
	}
	
	@GetMapping("/user/delete")
	public String userDelete(Model model,Principal principal)
	{
		SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
        this.userService.deleteUser(principal.getName());
        SecurityContextHolder.clearContext();
		
		return "redirect:/";
	}
	
	@PreAuthorize("isAuthenticated()")
    @GetMapping("/user/Favorites/{id}")
    public String addFavorites(Principal principal, @PathVariable("id") Integer id) {
        Post post = this.postService.getPost(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.userService.addFavorites(post, siteUser);
        return String.format("redirect:/post/board/detail/%s", id);
    }
	 
	@GetMapping("/user/FavoritesFind")
	public String favoritesFind(Model model,Principal principal)
	{
		SiteUser user =this.userService.getUser(principal.getName());
        model.addAttribute("user", user);
		return "user_favorites";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/user/FavoritesDelete/{id}")
	public String deleteFavorites(Principal principal, @PathVariable("id") Integer id)
	{
		Post post = this.postService.getPost(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.userService.deleteFavorites(post, siteUser);
        
		return String.format("redirect:/post/board/detail/%s", id);
	}
	

}
