package hubway.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import hubway.Station;

import java.util.List;

public interface StationRepository extends MongoRepository<Station, String> {
	public List<Station> findByMunicipality(String municipality);
}