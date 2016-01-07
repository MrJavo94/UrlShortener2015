package urlshortener2015.imperialred.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
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
	public long clicksByHash(String hash, Date from, Date to,
			Float min_latitude, Float max_longitude, Float max_latitude,
			Float min_longitude) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		Collection<Criteria> array = new ArrayList<Criteria>();
		if (from != null) {
			criteria = Criteria.where("created").gte(from);
			array.add(criteria);
		}
		if (to != null) {
			criteria = Criteria.where("created").lte(to);
			array.add(criteria);
		}
		if (min_latitude != null) {
			criteria = Criteria.where("latitude").gte(min_latitude);
			array.add(criteria);
		}
		if (max_longitude != null) {
			criteria = Criteria.where("longitude").lte(max_longitude);
			array.add(criteria);
		}
		if (max_latitude != null) {
			criteria = Criteria.where("latitude").lte(max_latitude);
			array.add(criteria);
		}
		if (min_longitude != null) {
			criteria = Criteria.where("longitude").gte(min_longitude);
			array.add(criteria);
		}
		/* Returns the aggregation */
		if (array.size() > 0) {
			criteria = Criteria.where("hash").is(hash)
					.andOperator(array.toArray(new Criteria[array.size()]));
		}
		else {
			criteria = Criteria.where("hash").is(hash);
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
			Float min_latitude, Float max_longitude, Float max_latitude,
			Float min_longitude) {
		/* Chooses a constraint for the query */
		Criteria criteria;
		Collection<Criteria> array = new ArrayList<Criteria>();
		if (from != null) {
			criteria = Criteria.where("created").gte(from);
			array.add(criteria);
		}
		if (to != null) {
			criteria = Criteria.where("created").lte(to);
			array.add(criteria);
		}
		if (min_latitude != null) {
			criteria = Criteria.where("latitude").gte(min_latitude);
			array.add(criteria);
		}
		if (max_longitude != null) {
			criteria = Criteria.where("longitude").lte(max_longitude);
			array.add(criteria);
		}
		if (max_latitude != null) {
			criteria = Criteria.where("latitude").lte(max_latitude);
			array.add(criteria);
		}
		if (min_longitude != null) {
			criteria = Criteria.where("longitude").gte(min_longitude);
			array.add(criteria);
		}
		/* Returns the aggregation */
		if (array.size() > 0) {
			criteria = Criteria.where("hash").is(url)
					.andOperator(array.toArray(new Criteria[array.size()]));
		}
		else {
			criteria = Criteria.where("hash").is(url);
		}
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