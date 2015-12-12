package urlshortener2015.imperialred.objects;

import java.net.URI;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class ShortURL {
	
	@Id
	private String hash;
	private String target;
	private URI uri;
	private Date created;
	private Date expire;
	private String owner;
	private Integer mode;
	private String ip;
	private String country;

	public ShortURL(String hash, String target, URI uri, Date created,
			Date expire, String owner, Integer mode, String ip, String country) {
		this.hash = hash;
		this.target = target;
		this.uri = uri;
		this.created = created;
		this.expire = expire;
		this.owner = owner;
		this.mode = mode;
		this.ip = ip;
		this.country = country;
	}

	public ShortURL() {
	}

	public String getHash() {
		return hash;
	}

	public String getTarget() {
		return target;
	}

	public URI getUri() {
		return uri;
	}

	public Date getCreated() {
		return created;
	}
	
	public Date getExpire() {
		return expire;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getMode() {
		return mode;
	}

	public String getIP() {
		return ip;
	}

	public String getCountry() {
		return country;
	}

}
