package uk.co.pluckier.model;

public class Forgot {
	private String email;
	private String uuid;
	private String created;

	public Forgot() {
	}

	public Forgot(String email, String uuid, String created) {
		this.email = email;
		this.uuid = uuid;
		this.created = created;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "Forgot{" +
				"email='" + email + '\'' +
				", uuid='" + uuid + '\'' +
				", created='" + created + '\'' +
				'}';
	}
}
