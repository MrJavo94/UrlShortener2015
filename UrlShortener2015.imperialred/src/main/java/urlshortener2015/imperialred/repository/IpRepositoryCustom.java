package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import urlshortener2015.imperialred.objects.Ip;

public interface IpRepositoryCustom {
	
	Ip findSubnet(BigInteger ip);
	
}
