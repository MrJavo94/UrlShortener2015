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

	public void setCreatedLinks(List<ShortURL> createdLinks) {
		this.createdLinks = createdLinks;
	}

	public void setAvailableLinks(List<ShortURL> availableLinks) {
		this.availableLinks = availableLinks;
	}

	@Override
	public String toString() {
		return "User[id=" + id + ", mail='" + mail + "', nick='" + nick + "', password='" + password 
				+ "', createdLinks=" + createdLinks + ", availableLinks=" + availableLinks + "]";
	}
	
}
