package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import urlshortener2015.imperialred.objects.Ip;
import urlshortener2015.imperialred.web.UrlShortenerControllerWithLogs;

@Repository 
public class IpRepositoryImpl implements IpRepositoryCustom {
	
	private static final Logger logger = LoggerFactory
			.getLogger(IpRepositoryImpl.class);
	
	@Autowired
    public MongoTemplate mongoTemplate;
	
	@Override
	public Ip findSubnet(BigInteger ip) {
		/* (min_ip > ip) and (max_ip < ip) */
		Query query = new BasicQuery("{$and: [{minip: {$lte: " +
				ip.toString() + "}}, {maxip: {$gte: "+ 
				ip.toString() +"}}]}");
		logger.info(query.toString());
		return mongoTemplate.findOne(query, Ip.class);
	}

}
