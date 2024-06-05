package com.mysite.web.S3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.mysite.web.post.PostForm;
import com.mysite.web.user.SiteUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class imageController {
	
	private final FileStore fileStore;
	private final TestFileRepository testFileRepository;
	private final imageService imageService; 
	
	
	
	
	@GetMapping("/s3/upload")
	public String go_s3(Model model)
	{
		Optional<Test_file> File=this.testFileRepository.findById(11);
		Test_file TestFile=File.get();
		String originalFilename=TestFile.getUploadFilename();
		String url=this.imageService.showFile(originalFilename);
		System.out.println(url);
		model.addAttribute("url",url);
		
		return "s3Upload";
	}
	
	@PostMapping("/s3/upload")
	public String StoreFile(@RequestParam(value = "attachfile", required = false) MultipartFile clsUpload)
	{
		Test_file testFile=new Test_file();
		try {
			testFile.setUrl(this.imageService.saveFile(clsUpload));
			UploadFile attachFile = fileStore.storeFile(clsUpload);
			System.out.println("uploadname: "+ attachFile.getUploadFilename());
	        System.out.println("storename: "+attachFile.getStoreFilename());
			testFile.setUploadFilename(attachFile.getUploadFilename());
			testFile.setStoreFilename(attachFile.getStoreFilename());
		   
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 this.testFileRepository.save(testFile);
		
		return "redirect:/";
	}
	
	@GetMapping("/s3/download")
	public ResponseEntity<UrlResource> downfile()
	{
		Optional<Test_file> File=this.testFileRepository.findById(11);
		Test_file TestFile=File.get();
		String originalFilename=TestFile.getUploadFilename();
		
		return this.imageService.downloadImage(originalFilename);
	}
	
	@GetMapping("/s3/delete")
	public String deleteimg()
	{
		
		Optional<Test_file> File=this.testFileRepository.findById(10);
		Test_file TestFile=File.get();
		String originalFilename=TestFile.getUploadFilename();
		
		this.imageService.deleteImage(originalFilename);
		
		return "redirect:/";
	}
	
	
	
	
	

}
