package com.mysite.web.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.mysite.web.DataNotFoundException;
import com.mysite.web.post.Post;
import com.mysite.web.post.PostRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService 
{
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	public SiteUser create(String username, String password, String name) 
	{
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        
        user.setName(name);
        user.setCreateDate(LocalDateTime.now());
        this.userRepository.save(user);
        return user;
    }
	
	public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
	
	 public void modifyPassword(SiteUser user, String password) {
		 	user.setPassword(passwordEncoder.encode(password));
	        user.setModifyDate(LocalDateTime.now());
	        this.userRepository.save(user);
	    }
	 
	 public void modifyName(SiteUser user, String name) {
	        user.setName(name);
	        
	        user.setModifyDate(LocalDateTime.now());
	        this.userRepository.save(user);
	    }
	
	 public void deleteUser(String username)
	 {
		 Optional<SiteUser> ouser=userRepository.findByusername(username);
		 SiteUser user = ouser.get();
		 this.userRepository.delete(user);
		 
		 
	 }
	 
	 public void addFavorites(Post post, SiteUser siteUser) {
	        siteUser.getFavorites().add(post);
	        this.userRepository.save(siteUser);
	    }
	
	 public void deleteFavorites(Post post, SiteUser siteUser) {
	        siteUser.getFavorites().remove(post);
	        this.userRepository.save(siteUser);
	    }
	
	
	
	
	        
}
