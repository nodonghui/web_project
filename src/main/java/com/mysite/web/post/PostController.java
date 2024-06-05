package com.mysite.web.post;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.security.Principal;

import com.mysite.web.file.LowCapacityFile;
import com.mysite.web.file.LowCapacityFileService;
import com.mysite.web.largeFile.LargeCapacityFile;
import com.mysite.web.largeFile.LargeCapacityFileService;
import com.mysite.web.user.SiteUser;
import com.mysite.web.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.mysite.web.comment.CommentForm;
@RequestMapping("/post")
@Controller
@RequiredArgsConstructor 
public class PostController {
	
	private final PostService postService;
	private final UserService userService;
	private final LowCapacityFileService lowCapacityFileService; 
	private final LargeCapacityFileService largeCapacityFileService; 
	
	@GetMapping("/board")
	public String list(Model model,@RequestParam(value="page",defaultValue="0") int page,
			@RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<Post> paging = this.postService.getList(page,kw);
		model.addAttribute("kw", kw);
        model.addAttribute("paging", paging);
        return "board";
    }
	
	@GetMapping(value = "/board/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,CommentForm CommentForm) {
		
		System.out.println("!!!!!!!!!!!!!!!!!!");
		Post post = this.postService.getPost(id);
		int fileTypeFlag=-1;
		if(post.getLowCapacityFile().size()>0)
		{
			System.out.println("low pass");
			String url=post.getLowCapacityFile().get(0).getUrl();
			
			//html 파일에 저용량 파일 확장자 뭔지 전달
			//0일 경우 txt파일
			String uploadFileName=post.getLowCapacityFile().get(0).getUploadFilename();
			System.out.println("filename: "+uploadFileName);
			int pos = uploadFileName.lastIndexOf(".");
			
			String ext=uploadFileName.substring(pos + 1);
			System.out.println("ext: "+ext);
			if(ext.equals("txt") || ext.equals("docx") ||ext.equals("hwp") || ext.equals("pdf"))
				fileTypeFlag=0;
			if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif"))
				fileTypeFlag=1;
			if(ext.equals("mp4") ||ext.equals("avi") || ext.equals("mov"))
				fileTypeFlag=2;
			
			
			model.addAttribute("uploadFileName",uploadFileName);
			model.addAttribute("url",url);
			
		}
		model.addAttribute("fileTypeFlag",fileTypeFlag);
		//대용량 파일을 포함한지 파악하기 위한 flag 변수
		int largeFileFlag=0;
		if(post.getLargeCapacityFile().size()>0) {
			largeFileFlag=1;	
		}
		System.out.println("largeFlag: "+largeFileFlag);
		System.out.println("filetypeFlag: "+fileTypeFlag);
		model.addAttribute("largeFileFlag",largeFileFlag);
        model.addAttribute("post", post);
        
        
        return "board_detail";
    }
	
	 @PreAuthorize("isAuthenticated()")
	 @GetMapping("/create")
	    public String postCreate(PostForm postForm) {
	        return "post_form";
	    }
	 
	 @PreAuthorize("isAuthenticated()")
	 @PostMapping("/create")
	    public String questionCreate(@Valid PostForm postForm, BindingResult bindingResult,Principal principal) {
	        if (bindingResult.hasErrors()) {
	            return "post_form";
	        }
	        SiteUser siteUser = this.userService.getUser(principal.getName());
	        //user 객체에 임시로 저장해논 largeFile 정보 가져와 사용
	        //user 객체에 임시로 저장해논 largeFile 객체 복사후 삭제 복사한 객체는 create메소드에 입력
	        LargeCapacityFile temp=null;
	        
	        if(siteUser.getLargeCapacityFile().size()>0) {
	        temp=siteUser.getLargeCapacityFile().get(0);
	        }
	        LargeCapacityFile largeCapacityFile = this.largeCapacityFileService.clearBuffer(temp);
	        
	        try {
				LowCapacityFile file =this.lowCapacityFileService.saveFileDB(postForm.getLowCapacityFile());
				
				this.postService.create(postForm.getTitle(), postForm.getContents(),siteUser,file,largeCapacityFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return "redirect:/post/board";
	    }
	 
	 @PreAuthorize("isAuthenticated()")
	    @GetMapping("/vote/{id}")
	    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
	        Post post = this.postService.getPost(id);
	        SiteUser siteUser = this.userService.getUser(principal.getName());
	        this.postService.vote(post, siteUser);
	        return String.format("redirect:/post/board/detail/%s", id);
	    }
	 
	 @PreAuthorize("isAuthenticated()")
	    @GetMapping("/modify/{id}")
	    public String questionModify(Model model,PostForm postForm, @PathVariable("id") Integer id, Principal principal) {
	        Post post = this.postService.getPost(id);
	        if(!post.getAuthor().getUsername().equals(principal.getName())) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
	        }
	        
	        int largeFileFlag=0;
			if(post.getLargeCapacityFile().size()>0) {
				largeFileFlag=1;
				model.addAttribute("largeFileFlag",largeFileFlag);
			}
	        
	        if(post.getLowCapacityFile().size()>0)
	        {
	        	String url=post.getLowCapacityFile().get(0).getUrl();
				System.out.println("url:" + url);
				model.addAttribute("url",url);
	        	
	        }
	        model.addAttribute("id", id);
	        postForm.setTitle(post.getTitle());
	        postForm.setContents(post.getContents());
	        return "modify_form";
	    }
	 
	 @PreAuthorize("isAuthenticated()")
	    @PostMapping("/modify/{id}")
	    public String postModify(@Valid PostForm postForm, BindingResult bindingResult, 
	            Principal principal, @PathVariable("id") Integer id) {
	        if (bindingResult.hasErrors()) {
	            return "post_form";
	        }
	        Post post = this.postService.getPost(id);
	        if (!post.getAuthor().getUsername().equals(principal.getName())) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");	        
	        }
	        
	        SiteUser siteUser = this.userService.getUser(principal.getName());
	       
	        
	        LargeCapacityFile temp=null;
	        
	        if(post.getLargeCapacityFile().size()>0)
        	{
        		LargeCapacityFile deleteFile=post.getLargeCapacityFile().get(0);
        		//디렉토리 파일 제거
        		this.largeCapacityFileService.deleteLargeFile(deleteFile);
        		//db객체 제거
        		
        		
        	}
	        
	        if(siteUser.getLargeCapacityFile().size()>0) {
	        	temp=siteUser.getLargeCapacityFile().get(0);
	        	
	        }
	        
	        LargeCapacityFile largeCapacityFile = this.largeCapacityFileService.clearBuffer(temp);
	        
	        try {
	        	//새로운 파일 저장후 원래 파일 삭제
	        	
				LowCapacityFile file =this.lowCapacityFileService.saveFileDB(postForm.getLowCapacityFile());
				if(file !=null)
	        	{
	        		System.out.println("amazon file upload ok!");
	        	}
				if(post.getLowCapacityFile().size()>0) {
					LowCapacityFile deleteFile=post.getLowCapacityFile().get(0);
					//s3 객체 제거
					
					this.lowCapacityFileService.deleteS3LowFile(deleteFile);
					//db 제거
					
					
				}
				
				this.postService.modify(post, postForm.getTitle(), postForm.getContents(),file,largeCapacityFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        
	        return String.format("redirect:/post/board/detail/%s", id);
	    }
	 
	 @PreAuthorize("isAuthenticated()")
	    @GetMapping("/delete/{id}")
	    public String postDelete(Principal principal, @PathVariable("id") Integer id) {
	        Post post = this.postService.getPost(id);
	        if (!post.getAuthor().getUsername().equals(principal.getName())) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
	        }
	        
	        if(post.getLowCapacityFile().size() >0) {
	        	LowCapacityFile file=post.getLowCapacityFile().get(0);
	        	this.lowCapacityFileService.deleteS3LowFile(file);
	        }
	        if(post.getLargeCapacityFile().size()>0) {
	        	this.largeCapacityFileService.deleteLargeFile(post.getLargeCapacityFile().get(0));
	        }
	        this.postService.delete(post);
	        return "redirect:/";
	    }
	 
	 

}
