package com.mysite.web.largeFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mysite.web.file.LowCapacityFile;
import com.mysite.web.file.LowCapacityFileRepository;
import com.mysite.web.post.Post;
import com.mysite.web.post.PostRepository;
import com.mysite.web.user.SiteUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.progress.Progress;

@Slf4j
@Service
@RequiredArgsConstructor
public class LargeCapacityFileService {
	
	 private final LargeCapacityFileRepository largeCapacityFileRepository;
	 private final FFmpeg fFmpeg;
	 private final FFprobe fFprobe;
	 String uploadsrc ="C:\\Users\\USER\\Desktop\\web\\video\\";
	
	 public boolean chunkUpload(MultipartFile file, int chunkNumber, int totalChunks,SiteUser user) throws IOException {
	    	// 파일 업로드 위치
	        String uploadDir = "video";
	        
	        File dir = new File(uploadDir);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
	        
	        

			// 임시 저장 파일 이름
	        String filename = file.getOriginalFilename() + ".part" + chunkNumber;

	        Path filePath = Paths.get(uploadDir, filename);
	        // 임시 저장
	        Files.write(filePath, file.getBytes());

			// 마지막 조각이 전송 됐을 경우
	        if (chunkNumber == totalChunks-1) {
	            String[] split = file.getOriginalFilename().split("\\.");
	            String outputFilename = UUID.randomUUID() + "." + split[split.length-1];
	            
	            Path outputFile = Paths.get(uploadDir, outputFilename);
	            //db에 파일정보 저장
	            String url=uploadsrc + outputFilename;
	            String uploadFilename=file.getOriginalFilename();
	            
	            if(user.getLargeCapacityFile().size()>0) {
	            	for(int i=0;i<user.getLargeCapacityFile().size();i++){
	            		this.largeCapacityFileRepository.delete(user.getLargeCapacityFile().get(i));
	            		this.deleteLargeFile(user.getLargeCapacityFile().get(i));
	            	}
	            }
	            	
	            
	            this.create(url, uploadFilename, outputFilename, user);
	            //
	            Files.createFile(outputFile);
	            
	            // 임시 파일들을 하나로 합침
	            for (int i = 0; i < totalChunks; i++) {
	                Path chunkFile = Paths.get(uploadDir, file.getOriginalFilename() + ".part" + i);
	                Files.write(outputFile, Files.readAllBytes(chunkFile), StandardOpenOption.APPEND);
	                // 합친 후 삭제
	                Files.delete(chunkFile);
	            }
	            System.out.println("outputFilename: " +outputFilename);
	            this.convertToHls(outputFilename);
	            log.info("File uploaded successfully");
	            return true;
	        } else {
	            return false;
	        }
	    }
	 
	 
	 
	 public void create(String url, String uploadName,String storeName,SiteUser user) {
		 	LargeCapacityFile largeCapacityFile =new LargeCapacityFile();
		 	//user객체에 임시저장
	        largeCapacityFile.setUser(user);
	        
	        largeCapacityFile.setUrl(url);
	        largeCapacityFile.setUploadFilename(uploadName);
	        largeCapacityFile.setStoreFilename(storeName);
	        largeCapacityFile.setCreateDate(LocalDateTime.now());
	        
	        this.largeCapacityFileRepository.save(largeCapacityFile);
	    }
	 
	 public LargeCapacityFile clearBuffer(LargeCapacityFile file)
	 {
		 if(file ==null) {
			return null;
		 }
		 LargeCapacityFile largeCapacityFile =new LargeCapacityFile();
		 largeCapacityFile.setUrl(file.getUrl());
	     largeCapacityFile.setUploadFilename(file.getUploadFilename());
	     largeCapacityFile.setStoreFilename(file.getStoreFilename());
	     largeCapacityFile.setCreateDate(LocalDateTime.now());
	   
	     this.largeCapacityFileRepository.save(largeCapacityFile);
	     
	     this.largeCapacityFileRepository.delete(file);
	     
	     
		 
		 return largeCapacityFile;
	 }
	 public void delete(LargeCapacityFile file) {
	        this.largeCapacityFileRepository.delete(file);
	    } 
	 
	 public void deleteLargeFile(LargeCapacityFile largeCapacityFile)
	  {
		 String storeDirName="hls" +largeCapacityFile.getStoreFilename();
		  File file=new File(largeCapacityFile.getUrl());
		  
		  file.delete();
		  
		  this.deleteFile(uploadsrc+storeDirName);
		  
		  
		  
		  
		  
			  
		  
	  }
	 
	 public void deleteFile(String path) {
			File deleteFolder = new File(path);

			if(deleteFolder.exists()){
				File[] deleteFolderList = deleteFolder.listFiles();
				
				for (int i = 0; i < deleteFolderList.length; i++) {
					if(deleteFolderList[i].isFile()) {
						deleteFolderList[i].delete();
					}else {
						deleteFile(deleteFolderList[i].getPath());
					}
					deleteFolderList[i].delete(); 
				}
				deleteFolder.delete();
			}
		}
	 
	 
	 public void convertToHls(String filename) {
	    	Path inputFilePath = Paths.get(uploadsrc + filename);
	    	Path outputFolderPath = Paths.get(uploadsrc + "hls" + filename);

	     // 화질 별 폴더 생성
	        File prefix = outputFolderPath.toFile();
	        File _1080 = new File(prefix, "1080");
	        File _720 = new File(prefix, "720");
	        File _480 = new File(prefix, "480");

	        if (!_1080.exists()) _1080.mkdirs();
	        if (!_720.exists()) _720.mkdirs();
	        if (!_480.exists()) _480.mkdirs();

	        FFmpegBuilder builder = new FFmpegBuilder()
	                .setInput(inputFilePath.toAbsolutePath().toString())
	                .addExtraArgs("-y")
	                .addOutput(outputFolderPath.toAbsolutePath() + "/%v/playlist.m3u8") // 출력 위치
	                .setFormat("hls")
	                .addExtraArgs("-hls_time", "10") // chunk 시간
	                .addExtraArgs("-hls_list_size", "0") 
	                .addExtraArgs("-hls_segment_filename", outputFolderPath.toAbsolutePath() + "/%v/output_%03d.ts") // ts 파일 이름 (ex: output_000.ts)
	                .addExtraArgs("-master_pl_name", "master.m3u8") // 마스터 재생 파일
	                .addExtraArgs("-map", "0:v")
	                .addExtraArgs("-map", "0:v")
	                .addExtraArgs("-map", "0:v")
	                .addExtraArgs("-var_stream_map", "v:0,name:1080 v:1,name:720 v:2,name:480") // 출력 매핑

					// 1080 화질 옵션
	                .addExtraArgs("-b:v:0", "5000k") //5000000bit -> 625kb/s
	                .addExtraArgs("-maxrate:v:0", "5000k")
	                .addExtraArgs("-bufsize:v:0", "10000k")
	                .addExtraArgs("-s:v:0", "1920x1080")
	                .addExtraArgs("-crf:v:0", "15")
	                .addExtraArgs("-b:a:0", "128k")

					// 720 화질 옵션
	                .addExtraArgs("-b:v:1", "2500k")
	                .addExtraArgs("-maxrate:v:1", "2500k")
	                .addExtraArgs("-bufsize:v:1", "5000k")
	                .addExtraArgs("-s:v:1", "1280x720")
	                .addExtraArgs("-crf:v:1", "22")
	                .addExtraArgs("-b:a:1", "96k")

					// 480 화질 옵션
	                .addExtraArgs("-b:v:2", "1000k")
	                .addExtraArgs("-maxrate:v:2", "1000k")
	                .addExtraArgs("-bufsize:v:2", "2000k")
	                .addExtraArgs("-s:v:2", "854x480")
	                .addExtraArgs("-crf:v:2", "28")
	                .addExtraArgs("-b:a:2", "64k")
	                .done();

	        run(builder);
	    }

	    private void run(FFmpegBuilder builder) {
	        FFmpegExecutor executor = new FFmpegExecutor(fFmpeg, fFprobe);

	        executor
	                .createJob(builder, progress -> {
	                    log.info("progress ==> {}", progress);
	                    if (progress.status.equals(Progress.Status.END)) {
	                        log.info("================================= JOB FINISHED =================================");
	                    }
	                })
	                .run();
	    }
	    
	    public File getHlsFile(String key, String filename) {
	    	System.out.println("src:" +uploadsrc + key + "\\" + filename);
	        return new File(uploadsrc + key + "\\" + filename);
	        
	    }
	    public File getHlsFileV2(String key, String resolution, String filename) {
	        return new File(uploadsrc + key + "\\"  + resolution + "\\" + filename);
	    }
	

}
