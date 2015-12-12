package urlshortener2015.imperialred.objects;

import java.math.BigInteger;
import java.util.Date;

import org.springframework.data.annotation.Id;

public class Click {

	
	@Id
	private BigInteger id;
	private String hash;
	private Date created;
	private String referrer;
	private String browser;
	private String platform;
	private String ip;
	private String country;

	public Click(BigInteger id, String hash, Date created, String referrer,
			String browser, String platform, String ip, String country) {
		this.id = id;
		this.hash = hash;
		this.created = created;
		this.referrer = referrer;
		this.browser = browser;
		this.platform = platform;
		this.ip = ip;
		this.country = country;
	}

	public BigInteger getId() {
		return id;
	}

	public String getHash() {
		return hash;
	}

	public Date getCreated() {
		return created;
	}

	public String getReferrer() {
		return referrer;
	}

	public String getBrowser() {
		return browser;
	}

	public String getPlatform() {
		return platform;
	}

	public String getIp() {
		return ip;
	}

	public String getCountry() {
		return country;
	}
	
    @Override
    public String toString() {
        return String.format(
                "Click[id=%s, hash='%s', date='%t', referrer='%s', browser='%s', platform='%s', ip='%s', country='%s']",
                id, hash, created, referrer, browser, platform, ip, country);
    }
}
