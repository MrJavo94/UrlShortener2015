package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.imperialred.objects.Ip;

public interface IpRepository extends MongoRepository<Ip, BigInteger>, IpRepositoryCustom {
	
	Ip save(Ip ip);
	
	void deleteAll();
	
	void delete(BigInteger id);
	
	long count();
	
}
