package com.mysite.web;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.web.post.PostService;





@SpringBootTest
class WebApplicationTests {

	@Autowired
	private PostService postService;
	
	@Test
	void testJpa() {
		
		for (int i = 1; i <= 200; i++) {
            String title = String.format("테스트 데이터입니다:[%03d]", i);
            String contents = "내용무";
            this.postService.create(title, contents,null,null,null);
        }
    }
	

}
