package uk.co.pluckier.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class Login {
	private String username;
	private String ip;
	private String at;
	
	public Login(String username, String ip, String at) {
		this.username = username;
		this.ip = ip;
		this.at = at;
	}

}
