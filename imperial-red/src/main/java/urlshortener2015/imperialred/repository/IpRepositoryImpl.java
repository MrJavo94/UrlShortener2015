package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import urlshortener2015.imperialred.objects.Ip;

@Repository
public class IpRepositoryImpl implements IpRepositoryCustom {
	
	@Autowired
    public MongoTemplate mongoTemplate;
	
	@Override
	public Ip findSubnet(BigInteger ip) {
		Query query = new Query(Criteria.where("minip").gte(ip)
				.andOperator(Criteria.where("maxip").lte(ip)));
		return mongoTemplate.findOne(new Query(new Criteria("{$and: [{minip: {$gte: "+ip+"}}, {maxip: {$lte: "+ip+"}}]}")), Ip.class, "ip");
	}

}
