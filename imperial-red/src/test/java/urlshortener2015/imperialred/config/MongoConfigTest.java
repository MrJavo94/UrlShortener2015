package urlshortener2015.imperialred.config;



import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodProcess;
//import de.flapdoodle.embed.mongo.MongodStarter;
//import de.flapdoodle.embed.mongo.config.IMongodConfig;
//import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.distribution.Version;


/*@Configuration
@EnableMongoRepositories(basePackageClasses=RepositoryPackage.class)
@ComponentScan(basePackageClasses=TestPackage.class)*/
/**
 * Config for EmbedMongo, used for testing.
 */
/*public class MongoConfigTest extends AbstractMongoConfiguration {
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();


    @Autowired(required = false)
    private MongoClientOptions options;

    @Override
    protected String getDatabaseName() {
        return "URL";
    }

    @Bean(destroyMethod = "close")
    public Mongo mongo() throws IOException {
        Net net = mongod().getConfig().net();
        //Create new mongoDB for testing
        System.out.println("Testing MONGODB");
        return new MongoClient(net.getServerAddress().getHostName() ,net.getPort() );
    }

    @Bean(destroyMethod = "stop")
    public MongodProcess mongod() throws IOException {
        return mongodExe().start();
    }

    @Bean(destroyMethod = "stop")
    public MongodExecutable mongodExe() throws IOException {
        return starter.prepare(mongodConfig());
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
    }
}*/