package com.mysite.web.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.mysite.web.post.Post;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class LowCapacityFileService {
	
	private final S3Store s3Store;
	private final LowCapacityFileRepository lowCapacityFileRepository;
	
	 
	 public LowCapacityFile saveFileDB(MultipartFile multipartFile) throws IOException
	 {
		 
		 LowCapacityFile File=new LowCapacityFile();
			try {
				UploadFile attachFile = s3Store.nameExt(multipartFile);
				if(attachFile ==null)
				 {
					 return null;
				 }
				File.setUrl(this.s3Store.saveFile(multipartFile,attachFile.getStoreFilename()));
				
				//System.out.println("uploadname: "+ attachFile.getUploadFilename());
		        //System.out.println("storename: "+attachFile.getStoreFilename());
				File.setUploadFilename(attachFile.getUploadFilename());
				File.setStoreFilename(attachFile.getStoreFilename());
			   
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 this.lowCapacityFileRepository.save(File);
			return File; 
			 
	 }

	  public void deleteS3LowFile(LowCapacityFile File)
	  {
		  String fileName=File.getStoreFilename();
		  
		  
		  this.s3Store.deleteImage(fileName);
	  }
	  
	  private String extractExt(String originalFilename) {
	        int pos = originalFilename.lastIndexOf(".");
	        return originalFilename.substring(pos + 1);
	    }
	  
	  public void delete(LowCapacityFile file) {
		  
		  
	        this.lowCapacityFileRepository.delete(file);
	        
	    }

	  
}
