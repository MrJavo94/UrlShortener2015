package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;


import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import urlshortener2015.imperialred.objects.Click;

@Repository
public class ClickRepositoryImpl implements ClickRepositoryCustom{
	
	@Autowired
    public MongoTemplate mongoTemplate;
	
	public List<Click> list(BigInteger limit, BigInteger offset){		
		return mongoTemplate.findAll(Click.class).subList(offset.intValue(),limit.intValue());		
	}

	@Override
	public long clicksByHash(String hash) {
		return mongoTemplate.count(query(where("hash").is(hash)), Click.class);
	}

	@Override
	public void update(Click cl) {
		mongoTemplate.save(cl);
	}

}
