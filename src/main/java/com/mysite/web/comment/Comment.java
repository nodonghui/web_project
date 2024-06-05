package com.mysite.web.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.web.post.Post;
import com.mysite.web.user.SiteUser;

import jakarta.persistence.CascadeType; 
import jakarta.persistence.OneToMany; 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//@ManyToOne
	//private SiteUser author;
	
	@ManyToOne
	private Post post;
	
	@Column(columnDefinition = "TEXT")
	private String contents;
	
	@ManyToOne
	private SiteUser author;
	
	@ManyToMany
    Set<SiteUser> voter;
	 
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;

}
