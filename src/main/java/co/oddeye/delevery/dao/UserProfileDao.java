package co.oddeye.delevery.dao;

import java.util.List;

import co.oddeye.delevery.model.UserProfile;


public interface UserProfileDao {

	List<UserProfile> findAll();
	
	UserProfile findByType(String type);
	
	UserProfile findById(int id);
}
