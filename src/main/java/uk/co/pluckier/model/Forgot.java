package uk.co.pluckier.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class Forgot {
	private String email;
	private String uuid;
	private String created;
	
	public Forgot(String email, String uuid, String created) {
		this.email = email;
		this.uuid = uuid;
		this.created = created;
	}

}
