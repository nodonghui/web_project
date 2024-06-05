package com.mysite.web.largeFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.mysite.web.post.Post;
import com.mysite.web.post.PostService;
import com.mysite.web.user.SiteUser;
import com.mysite.web.user.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LargeCapacityFileController {
	
	private final LargeCapacityFileService largeCapacityFileUploadService;
	private final PostService postService;
	private final UserService userService;
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/largefile/upload")
    public ResponseEntity<String> chunkUpload(@RequestParam("chunk") MultipartFile file,
                                              @RequestParam("chunkNumber") int chunkNumber,
                                              @RequestParam("totalChunks") int totalChunks,
                                              Principal principal) throws IOException {
		
		SiteUser siteUser = this.userService.getUser(principal.getName());
		
        boolean isDone = largeCapacityFileUploadService.chunkUpload(file, chunkNumber, totalChunks,siteUser);

        return isDone ?
                ResponseEntity.ok("File uploaded successfully") :
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
    }
	
	 
	 
	 @ResponseBody
		@GetMapping("/video/{id}/{filename}")
		public ResponseEntity<InputStreamResource> getMaster(@PathVariable("id") Integer id,
	            @PathVariable("filename") String filename) throws FileNotFoundException {
			
		 	Post post=this.postService.getPost(id);
	    	String key="hls"+post.getLargeCapacityFile().get(0).getStoreFilename();
			File file = largeCapacityFileUploadService.getHlsFile(key, filename);
	    	
	    	
	    	InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType("application/x-mpegURL"))
	                .body(resource);
	        
	    }
		
		
		@ResponseBody
	    @GetMapping("/video/{id}/{resolution}/{filename}")
	    public ResponseEntity<InputStreamResource> getPlaylist( @PathVariable("id") Integer id,
	            @PathVariable("resolution") String resolution,
	            @PathVariable("filename") String filename
	    ) throws FileNotFoundException {
			Post post=this.postService.getPost(id);
	    	String key="hls"+post.getLargeCapacityFile().get(0).getStoreFilename();
	        File file = largeCapacityFileUploadService.getHlsFileV2(key, resolution, filename);
	        
	        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType("application/x-mpegURL"))
	                .body(resource);
	    }
	 
	 
	 @GetMapping("/largefile/download/{id}")
		public ResponseEntity<Resource> downloadAttach(@PathVariable("id") Integer id) throws MalformedURLException {
			 //...itemId 이용해서 고객이 업로드한 파일 이름인 uploadFileName랑 서버 내부에서 사용하는 파일 이름인 storeFileName을 얻는다는 내용은 생략
			System.out.println("다운로드 전송 시작");
			Post post = this.postService.getPost(id);
			String url=post.getLargeCapacityFile().get(0).getUrl();
		    UrlResource resource = new UrlResource("file:" + url);
		    
		    //한글 파일 이름이나 특수 문자의 경우 깨질 수 있으니 인코딩 한번 해주기
		    String encodedUploadFileName = UriUtils.encode(url,StandardCharsets.UTF_8);
		    
		    //아래 문자를 ResponseHeader에 넣어줘야 한다. 그래야 링크를 눌렀을 때 다운이 된다.
		    //정해진 규칙이다.
		    String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
		   
		    return ResponseEntity.ok()
		 			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				 	.body(resource);
		}
	 
	 
	 	@GetMapping("/test")
	 	public String goTest()
	 	{
	 		return "test";
	 	}
	 
	 
	 	@ResponseBody
		@GetMapping("/test/{dirname}/{filename}")
		public ResponseEntity<InputStreamResource> getTestMaster(
	            @PathVariable("filename") String filename,
	            @PathVariable("dirname") String dirname) throws FileNotFoundException {
			
		 	
	    	
			File file = new File("C:\\Users\\USER\\Desktop\\web\\video\\"+ dirname + "\\" +filename);
	    	
			
	    	InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType("application/x-mpegURL"))
	                .body(resource);
	        
	    }
		
		
		@ResponseBody
	    @GetMapping("/test/{dirname}/{resolution}/{filename}")
	    public ResponseEntity<InputStreamResource> getTestPlaylist(
	            @PathVariable("resolution") String resolution,
	            @PathVariable("filename") String filename,
	            @PathVariable("dirname") String dirname
	    ) throws FileNotFoundException {
			
	    	
			File file = new File("C:\\Users\\USER\\Desktop\\web\\video\\" + dirname +"\\"+ resolution +"\\"+ filename);
	        
	        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType("application/x-mpegURL"))
	                .body(resource);
	    }
	 
	 

}
