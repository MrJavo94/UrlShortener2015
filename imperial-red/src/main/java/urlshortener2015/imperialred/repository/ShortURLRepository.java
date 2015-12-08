package urlshortener2015.imperialred.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import urlshortener2015.common.domain.ShortURL;

public interface ShortURLRepository extends MongoRepository<ShortURL, Long>, ShortURLRepositoryCustom{
	
	ShortURL findByKey(String id);

	List<ShortURL> findByTarget(String target);

	ShortURL save(ShortURL su);

	void delete(String id);

	long count();
	
}


