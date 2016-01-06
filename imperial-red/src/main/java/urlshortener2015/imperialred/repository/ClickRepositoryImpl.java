package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import urlshortener2015.imperialred.objects.Click;

@Repository
public class ClickRepositoryImpl implements ClickRepositoryCustom {

	@Autowired
	public MongoTemplate mongoTemplate;

	public List<Click> list(BigInteger limit, BigInteger offset) {
		return mongoTemplate.findAll(Click.class).subList(offset.intValue(),
				limit.intValue());
	}

	@Override
	public long clicksByHash(String hash, Date from, Date to) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		if (from == null && to == null) {
			criteria = Criteria.where("hash").is(hash);
		}
		else if (from == null) {
			criteria = Criteria.where("hash").is(hash)
					.andOperator(Criteria.where("created").lt(to));
		}
		else if (to == null) {
			criteria = Criteria.where("hash").is(hash)
					.andOperator(Criteria.where("created").gte(from));
		}
		else {
			criteria = Criteria.where("hash").is(hash).andOperator(
					Criteria.where("created").lt(to),
					Criteria.where("created").gte(from));
		}
		/* Returns count of clicks */
		return mongoTemplate.count(Query.query(criteria), Click.class);
	}

	@Override
	public void update(Click cl) {
		mongoTemplate.save(cl);
	}

	/**
	 * Gets the number of clicks to a URL, aggregated by countries. Optionally,
	 * a <from> date and a <to> date can be specified.
	 */
	public GroupByResults<Click> getClicksByCountry(String url, Date from,
			Date to) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		if (from == null && to == null) {
			criteria = Criteria.where("hash").is(url);
		}
		else if (from == null) {
			criteria = Criteria.where("hash").is(url)
					.andOperator(Criteria.where("created").lt(to));
		}
		else if (to == null) {
			criteria = Criteria.where("hash").is(url)
					.andOperator(Criteria.where("created").gte(from));
		}
		else {
			criteria = Criteria.where("hash").is(url).andOperator(
					Criteria.where("created").lt(to),
					Criteria.where("created").gte(from));
		}
		/* Returns the aggregation */
		return mongoTemplate.group(criteria, "click",
				GroupBy.key("country").initialDocument("{ count: 0}")
						.reduceFunction(
								"function(doc, prev) { prev.count += 1}"),
				Click.class);
	}

	@Override
	public GroupByResults<Click> getClicksByCity(String url, Date from, Date to,
			Float min_longitude, Float min_latitude, Float max_longitud,
			Float max_latitude) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		if (from == null && to == null) {
			criteria = Criteria.where("hash").is(url);
		}
		else if (from == null) {
			criteria = Criteria.where("hash").is(url)
					.andOperator(Criteria.where("created").lt(to));
		}
		else if (to == null) {
			criteria = Criteria.where("hash").is(url)
					.andOperator(Criteria.where("created").gte(from));
		}
		else {
			criteria = Criteria.where("hash").is(url).andOperator(
					Criteria.where("created").lt(to),
					Criteria.where("created").gte(from));
		}
		/* Returns the aggregation */
		return mongoTemplate.group(criteria, "click",
				GroupBy.key("city").initialDocument("{ count: 0}")
						.reduceFunction(
								"function(doc, prev) { prev.count += 1}"),
				Click.class);
	}

	@Override
	public long clicksByCity(String city, Date from, Date to) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		if (from == null && to == null) {
			criteria = Criteria.where("city").is(city);
		}
		else if (from == null) {
			criteria = Criteria.where("city").is(city)
					.andOperator(Criteria.where("created").lt(to));
		}
		else if (to == null) {
			criteria = Criteria.where("city").is(city)
					.andOperator(Criteria.where("created").gte(from));
		}
		else {
			criteria = Criteria.where("city").is(city).andOperator(
					Criteria.where("created").lt(to),
					Criteria.where("created").gte(from));
		}
		/* Returns count of clicks */
		return mongoTemplate.count(Query.query(criteria), Click.class);
	}

}