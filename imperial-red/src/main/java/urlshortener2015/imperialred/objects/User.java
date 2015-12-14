package urlshortener2015.imperialred.objects;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.annotation.Id;

public class User {
	
	@Id
	private BigInteger id;
	private String mail;
	private String nick;
	private String password;
	private List<ShortURL> createdLinks;
	private List<ShortURL> availableLinks;
	
	public User(BigInteger id, String mail, String nick, String password, 
			List<ShortURL> createdLinks, List<ShortURL> availableLinks) {
		this.id = id;
		this.mail = mail;
		this.nick = nick;
		this.password = password;
		this.createdLinks = createdLinks;
		this.availableLinks = availableLinks;
	}
	
	public User(String mail, String nick, String password, 
			List<ShortURL> createdLinks, List<ShortURL> availableLinks) {
		this.mail = mail;
		this.nick = nick;
		this.password = password;
		this.createdLinks = createdLinks;
		this.availableLinks = availableLinks;
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
	
	public List<ShortURL> getCreatedLinks() {
		return createdLinks;
	}
	
	public List<ShortURL> getAvailableLinks() {
		return availableLinks;
	}

	@Override
	public String toString() {
		return "User[id=" + id + ", mail='" + mail + "', nick='" + nick + "', password='" + password 
				+ "', createdLinks=" + createdLinks + ", availableLinks=" + availableLinks + "]";
	}
	
}
