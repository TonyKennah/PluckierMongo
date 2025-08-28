package uk.co.pluckier.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class User {
	private String username;
	private String password;
	private String email;
	private String freq;
	private String purchased;
	
	public User(String username, String password, String email, String freq, String purchased) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.freq = freq;
		this.purchased = purchased;
	}

}
