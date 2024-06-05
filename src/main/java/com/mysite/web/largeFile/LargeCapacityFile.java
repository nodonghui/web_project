package com.mysite.web.largeFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.web.comment.Comment;
import com.mysite.web.file.LowCapacityFile;
import com.mysite.web.post.Post;
import com.mysite.web.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LargeCapacityFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String url;
	
	
	
	private String uploadFilename;  // 작성자가 업로드한 파일명
    private String storeFilename;   // 중복되지않게 처리한 파일명
    
    private LocalDateTime createDate;
	private LocalDateTime modifyDate;
    
	@ManyToOne
	private Post post;
    
    @ManyToOne
	private SiteUser user;
}
