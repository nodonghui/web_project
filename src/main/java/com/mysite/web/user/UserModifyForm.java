package com.mysite.web.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModifyForm {
	
    private String password1;
    private String password2;
    
    private String name;

}
