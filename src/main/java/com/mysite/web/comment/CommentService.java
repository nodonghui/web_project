package com.mysite.web.comment;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mysite.web.DataNotFoundException;
import com.mysite.web.post.Post;
import com.mysite.web.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	
	
	public void create(Post post, String contents)
	{
		Comment comment =new Comment();
		comment.setContents(contents);
		comment.setCreateDate(LocalDateTime.now());
        comment.setPost(post);
        
        this.commentRepository.save(comment);
	}
	
	public Comment getComment(Integer id) {
        Optional<Comment> answer = this.commentRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Comment comment, String contents) {
        comment.setContents(contents);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }
	
	 public void vote(Comment comment, SiteUser siteUser) {
	        comment.getVoter().add(siteUser);
	        this.commentRepository.save(comment);
	    }

}
