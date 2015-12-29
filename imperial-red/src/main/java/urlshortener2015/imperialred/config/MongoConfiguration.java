package urlshortener2015.imperialred.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import urlshortener2015.imperialred.web.UrlShortenerControllerWithLogs;
import urlshortener2015.imperialred.web.WebPackage;

@Configuration
@EnableMongoRepositories(basePackageClasses=urlshortener2015.imperialred.repository.ClickRepository.class)
@ComponentScan(basePackageClasses=WebPackage.class)
@PropertySource("classpath:mongo.properties")
public class MongoConfiguration extends AbstractMongoConfiguration {
	
	
	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Value("${mongodb.ip}")
	String ip;
	@Value("${mongodb.puerto}")
	String puerto;
	@Value("${mongodb.user}")
	String usuario;
	@Value("${mongodb.pass}")
	String pass;
	@Value("${mongodb.db}")
	String db;
	

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		ServerAddress sv = new ServerAddress(ip, Integer.parseInt(puerto));
		MongoCredential credential = MongoCredential.createCredential(usuario, db, pass.toCharArray());
		MongoClient mongoClient = new MongoClient(sv, Arrays.asList(credential));
		logger.debug("MongoDbController.mongoDbFactory");
		return new SimpleMongoDbFactory(mongoClient, db);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}
	
	  @Override
	  protected String getDatabaseName() {
	    return db;
	  }

	  @Override
	  public MongoClient mongo() throws Exception {
		  ServerAddress sv = new ServerAddress(ip, Integer.parseInt(puerto));
			MongoCredential credential = MongoCredential.createCredential(usuario, db, pass.toCharArray());
			return new MongoClient(sv, Arrays.asList(credential));
	  }

	  @Override
	  protected String getMappingBasePackage() {
	    return "com.oreilly.springdata.mongodb";
	  }
}
