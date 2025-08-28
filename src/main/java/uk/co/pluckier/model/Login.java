package uk.co.pluckier.model;

public class Login {
	private String username;
	private String ip;
	private String at;

	public Login() {
	}

	public Login(String username, String ip, String at) {
		this.username = username;
		this.ip = ip;
		this.at = at;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	@Override
	public String toString() {
		return "Login{" +
				"username='" + username + '\'' +
				", ip='" + ip + '\'' +
				", at='" + at + '\'' +
				'}';
	}
}
