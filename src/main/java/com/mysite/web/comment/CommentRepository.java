package com.mysite.web.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer>{
	
	Optional<Comment> findById(Integer id);

}
