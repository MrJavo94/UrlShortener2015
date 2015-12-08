package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import urlshortener2015.imperialred.objects.ShortURL;

@Repository
public class ShortURLRepositoryImpl implements ShortURLRepositoryCustom{
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Override
	public List<ShortURL> list(BigInteger limit, BigInteger offset) {
		return mongoTemplate.findAll(ShortURL.class).subList(offset.intValue(), limit.intValue());		
	}	

	@Override
	public ShortURL mark(ShortURL urlSafe, boolean safeness) {
		urlSafe.setSafe(safeness);
		mongoTemplate.save(urlSafe);
		return urlSafe;
	}
	
	@Override
	public void update(ShortURL su) {
		mongoTemplate.save(su);		
	}

}
