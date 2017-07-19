package co.oddeye.delevery.service;

import java.util.List;

import co.oddeye.delevery.model.UserProfile;


public interface UserProfileService {

	UserProfile findById(int id);

	UserProfile findByType(String type);
	
	List<UserProfile> findAll();
	
}
