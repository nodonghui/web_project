package com.mysite.web.post;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.mysite.web.comment.Comment;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.Optional;
import com.mysite.web.DataNotFoundException;
import com.mysite.web.file.LowCapacityFile;
import com.mysite.web.file.LowCapacityFileRepository;
import com.mysite.web.largeFile.LargeCapacityFile;
import com.mysite.web.largeFile.LargeCapacityFileRepository;
import com.mysite.web.user.SiteUser;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	
	private final PostRepository PostRepository;
	private final LowCapacityFileRepository lowCapacityFileRepository;
	private final LargeCapacityFileRepository largeCapacityFileRepository;
	
	public Page<Post> getList(int page,String kw){
		 List<Sort.Order> sorts = new ArrayList<>();
	     sorts.add(Sort.Order.desc("createDate"));
		 Pageable pageable=PageRequest.of(page, 10,Sort.by(sorts));
		 Specification<Post> spec = search(kw);
		 return this.PostRepository.findAll(spec, pageable);
	 }
	
	
	
	public Post getPost(Integer id) {  
        Optional<Post> question = this.PostRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("Post not found");
        }
    }
	 
	public void create(String title, String contents,SiteUser user,LowCapacityFile lowFile,LargeCapacityFile largeCapacityFile) {
        Post p = new Post();
        p.setTitle(title);
        p.setContents(contents);
        p.setAuthor(user);
        if(lowFile !=null)
        	lowFile.setPost(p);
        if(largeCapacityFile !=null)
        	largeCapacityFile.setPost(p);
        
        p.setCreateDate(LocalDateTime.now());
        
        this.PostRepository.save(p);
    }
	
	public void vote(Post post, SiteUser siteUser) {
        post.getVoter().add(siteUser);
        this.PostRepository.save(post);
    }
	
	 public void modify(Post post, String title, String contents,LowCapacityFile lowFile,LargeCapacityFile largeCapacityFile) {
	        post.setTitle(title);
	        post.setContents(contents);
	        post.setModifyDate(LocalDateTime.now());
	        
	        
	        LowCapacityFile newLowFile=new LowCapacityFile();
	
	        if(lowFile!=null && post.getLowCapacityFile().size()>0){
	        	LowCapacityFile postLowFile=post.getLowCapacityFile().get(0);
	        	
	        	postLowFile.setStoreFilename(lowFile.getStoreFilename());
	        	postLowFile.setUploadFilename(lowFile.getUploadFilename());
	        	postLowFile.setUrl(lowFile.getUrl());
	        	postLowFile.setPost(post);
	        	this.lowCapacityFileRepository.save(postLowFile);
	        }
	        else if(lowFile !=null){
	        	
	        	newLowFile.setStoreFilename(lowFile.getStoreFilename());
	        	newLowFile.setUploadFilename(lowFile.getUploadFilename());
	        	newLowFile.setUrl(lowFile.getUrl());
	        	newLowFile.setPost(post);
	        	this.lowCapacityFileRepository.save(newLowFile);
	        }
	        
	        
	        LargeCapacityFile newLargeFile=new LargeCapacityFile();
	        

	        if(largeCapacityFile!=null && post.getLargeCapacityFile().size()>0) {
	        	LargeCapacityFile postLargeFile=post.getLargeCapacityFile().get(0);
	        	
	        	postLargeFile.setStoreFilename( largeCapacityFile.getStoreFilename());
	        	postLargeFile.setUploadFilename( largeCapacityFile.getUploadFilename());
	        	postLargeFile.setUrl( largeCapacityFile.getUrl());
	        	postLargeFile.setPost(post);
	        	this.largeCapacityFileRepository.save(postLargeFile);
	        	
	        } else if(largeCapacityFile !=null)
	        {
	        	
	        	newLargeFile.setStoreFilename(largeCapacityFile.getStoreFilename());
	        	newLargeFile.setUploadFilename(largeCapacityFile.getUploadFilename());
	        	newLargeFile.setUrl(largeCapacityFile.getUrl());
	        	newLargeFile.setPost(post);
	        	this.largeCapacityFileRepository.save(newLargeFile);
	        	
	        }
	        
	        
	        this.PostRepository.save(post);
	        
	    }
	 
	 public void delete(Post post) {
	        this.PostRepository.delete(post);
	    }
	 
	 
	 private Specification<Post> search(String kw) {
	        return new Specification<>() {
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Predicate toPredicate(Root<Post> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
	                query.distinct(true);  
	                Join<Post, SiteUser> u1 = p.join("author", JoinType.LEFT);
	                Join<Post, Comment> a = p.join("commentList", JoinType.LEFT);
	                Join<Comment, SiteUser> u2 = a.join("author", JoinType.LEFT);
	                return cb.or(cb.like(p.get("title"), "%" + kw + "%"), // 제목 
	                        cb.like(p.get("contents"), "%" + kw + "%"),      // 내용 
	                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자 
	                        cb.like(a.get("contents"), "%" + kw + "%"),      // 답변 내용 
	                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자 
	            }
	        };
	    }

}
