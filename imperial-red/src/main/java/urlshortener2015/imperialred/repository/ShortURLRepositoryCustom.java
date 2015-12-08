package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import urlshortener2015.imperialred.objects.ShortURL;

public interface ShortURLRepositoryCustom {
	
	List<ShortURL> list(BigInteger limit, BigInteger offset);
	
	void update(ShortURL su);
	
	ShortURL mark(ShortURL urlSafe, boolean safeness);

}
