package uk.co.pluckier.mongo;

import java.util.List;

import uk.co.pluckier.model.Forgot;
import uk.co.pluckier.model.Login;
import uk.co.pluckier.model.User;

public interface Repo extends AutoCloseable {
	
	List<User> getAll();
	
	List<User> getAllValid();
	
	User get(String userId);
	
	User getByEmail(String email);
	
	User getByFrequency(String freq);
	
	boolean updatePasswordViaEmail(String email, String password);
	
	boolean updatePassword(String username, String password);
	
	boolean updatePurchased(String username, String purchased);
	
	boolean updateDayPurchased(String username, String purchased);
	
	boolean verify(String username);
	
	boolean add(User user);
	
	boolean remove(String username);
	
	boolean isValid(User user);
	
	Forgot getForgot(String userId);
	
	Forgot getForgotByEmail(String email);
	
	boolean addForgot(Forgot user);
	
	boolean removeForgot(String email);
	
	boolean updateForgotCreated(String uuid, String created);
	
	boolean addLogin(Login login);
	
	Login getLogin(String userId);
	
	boolean updateLogin(String username, String ip, String at);
	
	boolean deletAllLogins();
	
	void close();
	
}
