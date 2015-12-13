package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapreduce.GroupByResults;

import urlshortener2015.imperialred.objects.Click;

public interface ClickRepositoryCustom {
	
	long clicksByHash(String hash);
	
	void update(Click cl);
	
	List<Click> list(BigInteger limit, BigInteger offset);
	
	GroupByResults<Click> getClicksByCountry(String url, Date from, Date to);
}
