package urlshortener2015.imperialred.objects;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class Ip {
	
	@Id
	private BigInteger id;
	private String subnet;
	private BigInteger minip;
	private BigInteger maxip;
	private String country;
	
	public Ip(BigInteger id, String subnet, BigInteger minip, 
			BigInteger maxip, String country) {
		this.id = id;
		this.subnet = subnet;
		this.minip = minip;
		this.maxip = maxip;
		this.country = country;
	}

	public BigInteger getId() {
		return id;
	}

	public String getSubnet() {
		return subnet;
	}

	public BigInteger getMinip() {
		return minip;
	}

	public BigInteger getMaxip() {
		return maxip;
	}

	public String getCountry() {
		return country;
	}
	
	public String toString() {
		return String.format(
                "Ip[id=%s, subnet='%s', minip='%t', maxip='%s', country='%s']",
                id, subnet, minip, maxip, country);
	}
	
}
