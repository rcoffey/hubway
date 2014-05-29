package hubway.repositories;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import hubway.repositories.StationRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

@Configuration
@EnableMongoRepositories(basePackages = "hubway.repositories",
      includeFilters = @ComponentScan.Filter(value = {StationRepository.class}, type = FilterType.ASSIGNABLE_TYPE))
public class repoConfig {

  public @Bean
  MongoTemplate mongoTemplate(Mongo mongo) throws UnknownHostException {
    return new MongoTemplate(mongo, "galaway");
  }

  public @Bean Mongo mongo() throws UnknownHostException {
    return new Mongo(new MongoURI("mongodb://Galaway:galaway1@ds049868.mongolab.com:49868/galaway"));
  }
}