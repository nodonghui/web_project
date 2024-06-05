package com.mysite.web.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@Service
@Component
public class imageService {
	private final AmazonS3 amazonS3;
	
	@Value("${cloud.aws.s3.bucket}")
    private String bucket;
	
	  public String saveFile(MultipartFile multipartFile) throws IOException {
	        String originalFilename = multipartFile.getOriginalFilename();
	        
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentLength(multipartFile.getSize());
	        metadata.setContentType(multipartFile.getContentType());

	        amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
	        return amazonS3.getUrl(bucket, originalFilename).toString();
	    }

	  public ResponseEntity<UrlResource> downloadImage(String originalFilename) {
		    UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, originalFilename));

		    String contentDisposition = "attachment; filename=\"" +  originalFilename + "\"";

		    // header에 CONTENT_DISPOSITION 설정을 통해 클릭 시 다운로드 진행
		    return ResponseEntity.ok()
		            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
		            .body(urlResource);

		}
	  
	  
	  
	  public String showFile(String filename)
	  {
		  String resourceUrl = amazonS3.getUrl(bucket, filename).toString();
		  return resourceUrl;
	  }
	  
	  
	  public void deleteImage(String originalFilename)  {
		    amazonS3.deleteObject(bucket, originalFilename);
		}
	  
	  private String extractExt(String originalFilename) {
	        int pos = originalFilename.lastIndexOf(".");
	        return originalFilename.substring(pos + 1);
	    }
	
	
}
