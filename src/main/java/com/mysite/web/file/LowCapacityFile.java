package com.mysite.web.file;

import com.mysite.web.post.Post;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LowCapacityFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String url;
	
	@ManyToOne
	private Post post;
	
	
	private String uploadFilename;  // 작성자가 업로드한 파일명
    private String storeFilename;   // 중복되지않게 처리한 파일명

}
