package com.mysite.web.file;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Store {
	
	private final AmazonS3 amazonS3;
	
	@Value("${cloud.aws.s3.bucket}")
    private String bucket;
	
	 private final String rootPath = System.getProperty("user.dir");
		//System.out.println(System.getProperty("user.dir"));
		//C:\intelProject\sts_work_space\web
		 
	private final String fileDir = rootPath + "\\files\\";
		
	public String getFullPath(String filename) { return fileDir + filename; }
	
	 public String saveFile(MultipartFile multipartFile,String loadupName) throws IOException {
	        //String originalFilename = multipartFile.getOriginalFilename();
	        
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentLength(multipartFile.getSize());
	        metadata.setContentType(multipartFile.getContentType());

	        amazonS3.putObject(bucket, loadupName, multipartFile.getInputStream(), metadata);
	        return amazonS3.getUrl(bucket, loadupName).toString();
	    }
	 
	 public UploadFile nameExt(MultipartFile multipartFile) throws IOException {

	        if(multipartFile.isEmpty()) {
	            return null;
	        }

	        String originalFilename = multipartFile.getOriginalFilename();
	        // 작성자가 업로드한 파일명 -> 서버 내부에서 관리하는 파일명
	        // 파일명을 중복되지 않게끔 UUID로 정하고 ".확장자"는 그대로
	        String storeFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

	        // 파일을 저장하는 부분 -> 파일경로 + storeFilename 에 저장
	        //System.out.println(getFullPath(storeFilename));
	        //System.out.println(originalFilename);
	        //System.out.println(storeFilename);
	        //multipartFile.transferTo(new File(getFullPath(storeFilename)));

	        return new UploadFile(originalFilename, storeFilename);
	    }
	 
	 private String extractExt(String originalFilename) {
	        int pos = originalFilename.lastIndexOf(".");
	        return originalFilename.substring(pos + 1);
	    }
	 
	 public ResponseEntity<byte[]> getObject(String storedFileName) throws IOException {
	        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, storedFileName));
	        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
	        byte[] bytes = IOUtils.toByteArray(objectInputStream);
	 
	        String fileName = URLEncoder.encode(storedFileName, "UTF-8").replaceAll("\\+", "%20");
	        HttpHeaders httpHeaders = new HttpHeaders();
	        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        httpHeaders.setContentLength(bytes.length);
	        httpHeaders.setContentDispositionFormData("attachment", fileName);
	 
	        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	 
	    }
	  
	  
	  
	  
	  public void deleteImage(String originalFilename)  {
		    amazonS3.deleteObject(bucket, originalFilename);
		}

}
