package urlshortener2015.imperialred.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import urlshortener2015.imperialred.objects.Click;

@Repository
public class ClickRepositoryImpl implements ClickRepositoryCustom{
	
	@Autowired
    public MongoTemplate mongoTemplate;
	
	public List<Click> list(Long limit, Long offset){		
		return mongoTemplate.findAll(Click.class).subList(Math.toIntExact(offset), Math.toIntExact(limit));		
	}

	@Override
	public Long clicksByHash(String hash) {
		
		String mapFunction = "function() {  " + 
								"if (this.hash == '" + hash + "')" + 
									"emit(this.cust_id, 1);}";
		
		String reduceFunction = "function(keyCustId, valuesPrices) {" +
                          "return Array.sum(valuesPrices);};";
		
		mongoTemplate.mapReduce("click", mapFunction, reduceFunction, Long.class);
		return null;
	}

	@Override
	public void update(Click cl) {
		mongoTemplate.save(cl);
	}

}
