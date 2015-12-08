package urlshortener2015.common.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import urlshortener2015.common.domain.Click;

@Repository
public interface ClickRepository {

	List<Click> findByHash(String hash);

	Long clicksByHash(String hash);

	Click save(Click cl);

	void update(Click cl);

	void delete(Long id);

	void deleteAll();

	Long count();

	List<Click> list(Long limit, Long offset);
}
