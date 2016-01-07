package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapreduce.GroupByResults;

import urlshortener2015.imperialred.objects.Click;

public interface ClickRepositoryCustom {

	long clicksByHash(String hash, Date from, Date to, Float min_latitude,
			Float max_longitude, Float max_latitude, Float min_longitude);

	void update(Click cl);

	List<Click> list(BigInteger limit, BigInteger offset);

	GroupByResults<Click> getClicksByCountry(String url, Date from, Date to);

	long clicksByCity(String city, Date from, Date to);

	GroupByResults<Click> getClicksByCity(String city, Date from, Date to,
			Float min_latitude, Float max_longitude, Float max_latitude,
			Float min_longitude);
}
