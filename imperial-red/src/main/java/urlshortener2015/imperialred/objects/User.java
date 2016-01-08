package urlshortener2015.imperialred.objects;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class User {
	
	@Id
	private BigInteger id;
	private String mail;
	private String password;
	private Serializable provider;
	
	public User() {
		/*
		 * Default constructor is necessary for JSON to object conversion
		 */
	}
	
	public User(String mail, String password) {
		this.mail = mail;
		this.password = password;
	}
	
	public User(String mail, Serializable provider) {
		this.mail = mail;
		this.provider = provider;
	}

	public BigInteger getId() {
		return id;
	}
	
	public String getMail() {
		return mail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Serializable getProvider() {
		return provider;
	}


	public void setId(BigInteger id) {
		this.id = id;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setProvider(Serializable provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "User[id=" + id + ", mail='" + mail + "', password='" + password 
				+ "', provider='" + provider + "]";
	}
}
