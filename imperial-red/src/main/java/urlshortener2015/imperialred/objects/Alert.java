package urlshortener2015.imperialred.objects;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class Alert {
	
	@Id
	private BigInteger id;
	private String mail;
	private String hash;
	private Date date;
	
	public Alert(String mail, String hash, Date date) {
		this.mail = mail;
		this.hash = hash;
		this.date = date;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
