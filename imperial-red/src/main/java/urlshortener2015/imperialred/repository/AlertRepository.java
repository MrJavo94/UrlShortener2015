package urlshortener2015.imperialred.repository;

import java.math.BigInteger;

import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.imperialred.objects.Alert;

public interface AlertRepository extends MongoRepository<Alert, BigInteger> {
	
	Alert save(Alert a);
	
	Alert findFirstByOrderByDate();
	
}
