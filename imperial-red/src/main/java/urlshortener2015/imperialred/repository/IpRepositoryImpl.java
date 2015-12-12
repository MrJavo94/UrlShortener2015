package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import urlshortener2015.imperialred.objects.Ip;

@Repository 
public class IpRepositoryImpl implements IpRepositoryCustom {
	
	@Autowired
    public MongoTemplate mongoTemplate;
	
	@Override
	public Ip findSubnet(BigInteger ip) {
		/* (min_ip > ip) and (max_ip < ip) */
		Query query = new BasicQuery("{$and: [{minip: {$gte: " +
				ip.toString() + "}}, {maxip: {$lte: "+ 
				ip.toString() +"}}]}");
		return mongoTemplate.findOne(query, Ip.class);
	}

}
