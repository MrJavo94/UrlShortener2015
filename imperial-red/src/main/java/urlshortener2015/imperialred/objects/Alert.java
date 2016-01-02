package urlshortener2015.imperialred.objects;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class Alert {
	
	@Id
	private BigInteger id;
	private String mail;
	private String url;
	private Date date;
	
	public Alert(String mail, String url, Date date) {
		this.mail = mail;
		this.url = url;
		this.date = date;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
