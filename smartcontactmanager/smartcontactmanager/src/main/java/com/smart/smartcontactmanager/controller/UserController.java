package com.smart.smartcontactmanager.controller;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.smartcontactmanager.dao.ContactRepo;
import com.smart.smartcontactmanager.dao.UserRepo;
import com.smart.smartcontactmanager.entity.Contact;
import com.smart.smartcontactmanager.entity.User;
import com.smart.smartcontactmanager.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	@ModelAttribute
	public void CommonData(Model model, Principal principal) {
		String userName = principal.getName();

		User user = userRepo.getUserByName(userName);

		System.out.println("user" + user);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {

		return "normal/user_dashboard";

	}

	// -----------------------------------------
	@GetMapping("/add-contact")
	public String addcontact(Model model) {

		model.addAttribute("title", "ADD-Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";

	}
	// ADD Contact =====================================

	@PostMapping("/process-contact")
	private String processContact(@ModelAttribute Contact contact, @RequestParam("pimg") MultipartFile multipartFile,
			Principal principal, HttpSession session) {

		try {

			String name = principal.getName();

			User user = userRepo.getUserByName(name);
			/*
			 * if (multipartFile.isEmpty()) {
			 * 
			 * 
			 * 
			 * } else { contact.setImg(multipartFile.getOriginalFilename());
			 * 
			 * // Define the folder where you want to save the uploaded files String
			 * uploadFolderPath = "static/img";
			 * 
			 * // Create a unique filename to avoid overwriting existing files with the same
			 * name String uniqueFileName = multipartFile.getOriginalFilename();
			 * 
			 * // Define the file path for the uploaded file Path filePath =
			 * Paths.get(uploadFolderPath + uniqueFileName);
			 * 
			 * // Save the file to the specified location
			 * Files.copy(multipartFile.getInputStream(), filePath,
			 * StandardCopyOption.REPLACE_EXISTING);
			 * 
			 * // You can also use FileCopyUtils.copy() to copy the file instead of
			 * Files.copy(): // FileCopyUtils.copy(multipartFile.getInputStream(), new
			 * FileOutputStream(filePath.toFile()));
			 * 
			 * 
			 * 
			 * 
			 * }
			 */

			contact.setUser(user);

			user.getContacts().add(contact);

			userRepo.save(user);

			System.out.println("Done");
			System.out.println("DATA" + contact);

			session.setAttribute("message", new Message("Contact Saved Successfully......", "success"));

		} catch (Exception e) {

			/*
			 * session.setAttribute("message", new
			 * Message("Something went Wrong!!Try agin !!.."+e.getMessage(), "denger"));
			 */

		}

		return "normal/add_contact";

	}
	// Show Contact/////////////////////////////////////////////////////////////////

	@GetMapping("/show-contacts/{page}")
	private String showcontatcts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "Show-Conctact");
		/*
		 * String userName = principal.getName();
		 * 
		 * User user = userRepo.getUserByName(userName);
		 * 
		 * List<Contact>contacts=user.getContacts();
		 */
		String userName = principal.getName();

		User user = userRepo.getUserByName(userName);

		// current page
		// contact per page

		Pageable pageable = PageRequest.of(page, 10);

		Page<Contact> contacts = contactRepo.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";

	}

	// Show Contact detail.............................................

	@RequestMapping("/{cid}/contact")
	private String ShowDetail(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		System.out.println(cid);

		Optional<Contact> contactOptional = contactRepo.findById(cid);
		Contact contact = contactOptional.get();

		String userName = principal.getName();

		User user = userRepo.getUserByName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());

		}

		return "normal/contact_detail";

	}

	// TO delete the Contact........................................
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session,Principal principal) {

		Contact contact = contactRepo.findById(cid).get();
		

		User user = userRepo.getUserByName(principal.getName());
		
		user.getContacts().remove(contact);
		
		userRepo.save(user);
		

		

		session.setAttribute("message", new Message("Contact Deleted Successfully......", "success"));

		return "redirect:/user/show-contacts/0";

	}

	
	
	// To form update the Contact handler ............................

	@PostMapping("/update-contact/{cid}")
	public String updateFrom(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "Update Contact");

		Contact contact = contactRepo.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_from";

	}

	// To update the Contact handler ............................

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, Model model,Principal principal,HttpSession session) {
		
		
		
		try {
			User user = userRepo.getUserByName(principal.getName());
			
			contact.setUser(user);
			
			contactRepo.save(contact);
			
			session.setAttribute("message", new Message("Contact Update Successfully......", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
			
			session.setAttribute("message", new
					  Message("Something went Wrong!!Try agin !!.."+e.getMessage(), "denger"));
		}
		
			
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	//Your Profile
	
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		
		model.addAttribute("title", "Profile");
		
		
		return"normal/profile";
		
	}

}
