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
	private String city;
	private Float latitude;
	private Float longitude;

	public Click(BigInteger id, String hash, Date created, String referrer,
			String browser, String platform, String ip, String country,
			String city, Float longitude, Float latitude) {
		this.id = id;
		this.hash = hash;
		this.created = created;
		this.referrer = referrer;
		this.browser = browser;
		this.platform = platform;
		this.ip = ip;
		this.country = country;
		this.city = city;
		this.longitude = longitude;
		this.latitude = latitude;
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

	public String getCity() {
		return city;
	}

	public Float getLatitude() {
		return latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	@Override
	public String toString() {
		return String.format(
				"Click[id=%s, hash='%s', date='%t', referrer='%s', browser='%s', platform='%s', ip='%s', country='%s',"
						+ " city='%s', longitude='%d', latitude='%d']",
				id, hash, created, referrer, browser, platform, ip, country,
				city, longitude, latitude);
	}
}
