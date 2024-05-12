package com.mysite.web.post;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.Optional;
import com.mysite.web.DataNotFoundException;



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	
	private final PostRepository PostRepository;
	
	public Page<Post> getList(int page){
		 List<Sort.Order> sorts = new ArrayList<>();
	     sorts.add(Sort.Order.desc("createDate"));
		 Pageable pageable=PageRequest.of(page, 10,Sort.by(sorts));
		 return this.PostRepository.findAll(pageable);
	 }
	
	public Post getPost(Integer id) {  
        Optional<Post> question = this.PostRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("Post not found");
        }
    }
	 
	public void create(String title, String contents) {
        Post p = new Post();
        p.setTitle(title);
        p.setContents(contents);
        p.setCreateDate(LocalDateTime.now());
        
        this.PostRepository.save(p);
    }

}
