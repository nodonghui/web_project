package com.mysite.web.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




public interface PostRepository extends JpaRepository<Post, Integer> {
	
	
	Page<Post> findAll(Specification<Post> spec, Pageable pageable);
	
	
	

}
