package co.oddeye.delevery.dao;

import java.util.List;

import co.oddeye.delevery.model.User;


public interface UserDao {

	User findById(int id);
	
	User findBySSO(String sso);
	
	void save(User user);
	
	void deleteBySSO(String sso);
	
	List<User> findAllUsers();

}

