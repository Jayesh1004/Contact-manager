package com.smart.smartcontactmanager.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.smartcontactmanager.entity.Contact;

public interface ContactRepo extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:id")
	//current page 
	//contact per page
	public Page<Contact> findContactsByUser(@Param("id") int id,Pageable pageable);

}
