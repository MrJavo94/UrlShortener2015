package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.List;

import urlshortener2015.imperialred.objects.Click;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ClickRepository extends MongoRepository<Click, BigInteger>, ClickRepositoryCustom{
	
	List<Click> findByHash(String hash);
	
	long countByHash(String hash);

	Click save(Click cl);

	void deleteAll();
		
	void delete(BigInteger id);

	long count();
}

