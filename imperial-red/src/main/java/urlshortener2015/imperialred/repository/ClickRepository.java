package urlshortener2015.imperialred.repository;

import java.util.List;

import urlshortener2015.imperialred.objects.Click;

import org.springframework.data.mongodb.repository.MongoRepository;


//public interface ClickRepository extends MongoRepository<Click, String>{
public interface ClickRepository {

	List<Click> findByHash(String hash);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	long count();

	List<Click> list(Long limit, Long offset);
}

