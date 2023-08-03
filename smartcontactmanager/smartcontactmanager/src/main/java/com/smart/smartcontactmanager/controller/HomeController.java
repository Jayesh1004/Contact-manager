package com.smart.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.smartcontactmanager.dao.UserRepo;
import com.smart.smartcontactmanager.entity.User;
import com.smart.smartcontactmanager.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private UserRepo userRepo;
	
	
	//Home Url...........................
	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 

	@RequestMapping("/home")
	public String home(Model module) {
		module.addAttribute("title", "Home-Conctact");
		return "home";

	}
	
	//signup url..........................

	@RequestMapping("/signup")
	public String signup(Model module) {
		module.addAttribute("title", "Register");
		module.addAttribute("user", new User());

		return "signup";

	}
	
	
	
	//signup from submit here...........................

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("YOU HAVE NOT AGREED THE TERMS");

				throw new Exception("YOU HAVE NOT AGREED THE TERMS");

			}
			if (result.hasErrors()) {

				System.out.println("ERROR" + result.toString());
				model.addAttribute("user", user);

				return "signup";

			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImagurl("default.png");
		    user.setPassword(passwordEncoder.encode(user.getPassword()));
			

			System.out.println("aggrement" + agreement);
			System.out.println("user" + user);

			User result1 = this.userRepo.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Resistered !!..", "alert-success"));

			return "redirect:/signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);

			session.setAttribute("message", new Message("Somethink went Werong !!.." + e.getMessage(), "alert-denger"));

			return "signup";

		}

	}

	
	//Login from url..............................
	 @GetMapping("/signin") 
	public String signin(Model module) {
		 
		 module.addAttribute("title", "Login page");
		return "signin";
		
	}
	 
	
	
}
