package com.mysite.web.user;


import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public SiteUser create(String user_id, String password, String name) {
        SiteUser user = new SiteUser();
        user.setUserid(user_id);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setCreateDate(LocalDateTime.now());
        this.userRepository.save(user);
        return user;
    }
	
	

}
