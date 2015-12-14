package urlshortener2015.imperialred.objects;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.annotation.Id;

public class User {
	
	@Id
	private BigInteger id;
	private String mail;
	private String user;
	private String password;
	private List<ShortURL> createdLinks;
	private List<ShortURL> availableLinks;
	
	public String getMail() {
		return mail;
	}
	
	public String getUser() {
		return user;
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
		return "User[id=" + id + ", mail='" + mail + "', user='" + user + "', password='" + password 
				+ "', createdLinks=" + createdLinks + ", availableLinks=" + availableLinks + "]";
	}
	
}
