package uk.co.pluckier.model;

import java.util.Objects;

public class User {
	private String username;
	private String password;
	private String email;
	private String freq;
	private String purchased;
	
	public User() {
	}

	public User(String username, String password, String email, String freq, String purchased) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.freq = freq;
		this.purchased = purchased;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getPurchased() {
		return purchased;
	}

	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(username, user.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public String toString() {
		return "User{" + "username='" + username + '\'' + ", email='" + email + '\'' + ", freq='" + freq + '\'' + ", purchased='" + purchased + '\'' + '}';
	}
}
