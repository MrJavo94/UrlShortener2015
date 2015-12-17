package urlshortener2015.imperialred.repository;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.imperialred.objects.ShortURL;

public interface ShortURLRepository extends MongoRepository<ShortURL, String>, ShortURLRepositoryCustom{
	
	//@Cacheable("shortUrl")
	ShortURL findByHash(String id);

	List<ShortURL> findByTarget(String target);

	ShortURL save(ShortURL su);

	void delete(String id);

	long count();
	
}


