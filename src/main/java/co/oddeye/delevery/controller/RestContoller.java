package co.oddeye.delevery.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import co.oddeye.delevery.model.User;
import co.oddeye.delevery.model.UserProfile;
import co.oddeye.delevery.service.UserProfileService;
import co.oddeye.delevery.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@SessionAttributes("roles")
public class RestContoller {

    @Autowired
    UserService userService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    @Autowired
    AuthenticationTrustResolver authenticationTrustResolver;

	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	private String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}    
    
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public ResponseEntity<String> UserName() {
        List<User> users = userService.findAllUsers();                
        return new ResponseEntity<>(getPrincipal(), HttpStatus.OK);                
    }        
        
    /**
     * This method will list all existing users.
     */
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    public ResponseEntity<List<User>> listUsers() {
        List<User> users = userService.findAllUsers();                
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);                
    }

    /**
     * This method will be called on form submission, handling POST request for
     * saving user in database. It also validates the user input
     */
    @RequestMapping(value = {"/newuser"}, method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        if (!userService.isUserSSOUnique(user.getId(), user.getSsoId())) {            
            return new ResponseEntity<>(HttpStatus.CONFLICT);            
        }

        userService.saveUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * This method will be called on form submission, handling POST request for
     * updating user in database. It also validates the user input
     */
    @RequestMapping(value = {"/edit-user-{ssoId}"}, method = RequestMethod.POST)
    public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        System.out.println("Updating User " + id);
          
        User currentUser = userService.findById(id);
          
        if (currentUser==null) {
            System.out.println("User with id " + id + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
  
        currentUser.setEmail(user.getEmail());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
          
        userService.updateUser(currentUser);
        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }    

    /**
     * This method will delete an user by it's SSOID value.
     */
    @RequestMapping(value = {"/delete-user-{ssoId}"}, method = RequestMethod.GET)
    public ResponseEntity<User> deleteUser(@PathVariable("ssoId") String ssoId) {
        System.out.println("Fetching & Deleting User with id " + ssoId);
  
        User user = userService.findBySSO(ssoId);
        if (user == null) {
            System.out.println("Unable to delete. User with id " + ssoId + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
  
        userService.deleteUserBySSO(ssoId);
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method will provide UserProfile list to views
     */
    @ModelAttribute("roles")
    public List<UserProfile> initializeProfiles() {
        return userProfileService.findAll();
    }


}
