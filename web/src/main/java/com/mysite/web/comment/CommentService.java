package com.mysite.web.comment;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mysite.web.post.Post;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	
	private final CommentRepository answerRepository;
	
	
	public void create(Post post, String contents)
	{
		Comment comment =new Comment();
		comment.setContents(contents);
		comment.setCreateDate(LocalDateTime.now());
        comment.setPost(post);
        
        this.answerRepository.save(comment);
	}

}
