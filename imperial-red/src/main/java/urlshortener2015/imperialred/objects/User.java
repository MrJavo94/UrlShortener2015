package urlshortener2015.imperialred.objects;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class User {
	
	@Id
	private BigInteger id;
	private String mail;
	private String nick;
	private String password;
	private String twitter;
	
	public User(String mail, String nick, String password, String twitter) {
		this.mail = mail;
		this.nick = nick;
		this.password = password;
		this.twitter = twitter;
	}
	
	public BigInteger getId() {
		return id;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getTwitter() {
		return twitter;
	}


	public void setId(BigInteger id) {
		this.id = id;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	@Override
	public String toString() {
		return "User[id=" + id + ", mail='" + mail + "', nick='" + nick + "', password='" + password 
				+ "', twitter='" + twitter + "]";
	}
}
