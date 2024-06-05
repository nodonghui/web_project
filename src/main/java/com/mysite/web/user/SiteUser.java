package com.mysite.web.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.web.largeFile.LargeCapacityFile;
import com.mysite.web.post.Post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true)
	    private String username;

	    private String password;

	    private String name;
	    
	    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE) 
		private List<Post> postList; 
	    
	    @ManyToMany
	    Set<Post> Favorites;
	    
	    private LocalDateTime createDate;
		private LocalDateTime modifyDate;
		
		@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE) 
		private List<LargeCapacityFile> LargeCapacityFile;
		

}
