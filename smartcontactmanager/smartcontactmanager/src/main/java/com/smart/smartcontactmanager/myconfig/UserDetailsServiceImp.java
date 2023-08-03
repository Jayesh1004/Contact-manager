package com.smart.smartcontactmanager.myconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.smartcontactmanager.dao.UserRepo;
import com.smart.smartcontactmanager.entity.User;

public class UserDetailsServiceImp implements UserDetailsService {
	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// fetching user from database...................
		
		User user=userRepo.getUserByName(username);
		
		if (user==null) {
			throw new UsernameNotFoundException("Could not found User!!");
			
		}
		

		return new CustomUserDetails(user);
	}

}
