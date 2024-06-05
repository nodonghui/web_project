package com.mysite.web.post;

import java.time.LocalDateTime;
import java.util.List;

import com.mysite.web.comment.Comment;
import com.mysite.web.file.LowCapacityFile;
import com.mysite.web.largeFile.LargeCapacityFile;

import jakarta.persistence.CascadeType; 
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import com.mysite.web.user.SiteUser;
import java.util.Set;
import jakarta.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Post {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//@ManyToOne
	//private SiteUser author;
	
	private int media_classification;
	
	private int year_classification;
	
	@Column(length =200)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String contents;
	
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE) 
	private List<Comment> commentList; 
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE) 
	private List<LowCapacityFile> LowCapacityFile;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE) 
	private List<LargeCapacityFile> largeCapacityFile;
	
	
	
	 
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	
	@ManyToOne
    private SiteUser author;
	
	@ManyToMany
    Set<SiteUser> voter;
	 
	
	 
	

}
