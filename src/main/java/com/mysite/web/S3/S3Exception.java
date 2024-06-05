package com.mysite.web.S3;

public class S3Exception extends RuntimeException {
	
	S3Exception(){
		
	}
	
	S3Exception(String message)
	{
		super(message);
	}

}
