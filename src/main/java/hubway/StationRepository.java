package hubway;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StationRepository extends MongoRepository<Station, String> {
	
}