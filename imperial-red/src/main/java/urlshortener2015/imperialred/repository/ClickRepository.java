package urlshortener2015.imperialred.repository;

import java.util.List;

import urlshortener2015.imperialred.objects.Click;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;


public interface ClickRepository extends MongoRepository<Click, Long>, ClickRepositoryCustom{
	
	List<Click> findByHash(String hash);

	Click save(Click cl);

	void deleteAll();
		
	void delete(Long id);

	long count();
}

