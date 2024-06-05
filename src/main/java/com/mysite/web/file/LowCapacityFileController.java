package com.mysite.web.file;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.mysite.web.post.Post;
import com.mysite.web.post.PostService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class LowCapacityFileController {
	
	private final S3Store s3Store;
	private final PostService postService;
	
	@GetMapping("/lowfile/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") Integer id) throws IOException {
    	
    	
    	
    	Post post = this.postService.getPost(id);
		String fileName=post.getLowCapacityFile().get(0).getStoreFilename();
    	return s3Store.getObject(fileName);
    }
	
	

}
