package urlshortener2015.imperialred.repository;

import java.util.List;

import urlshortener2015.imperialred.objects.Click;



public interface ClickRepositoryCustom {
	
	Long clicksByHash(String hash);
	
	void update(Click cl);
	
	List<Click> list(Long limit, Long offset);
}
