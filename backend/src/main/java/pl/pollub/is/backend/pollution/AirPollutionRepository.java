package pl.pollub.is.backend.pollution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AirPollutionRepository extends JpaRepository<AirPollution, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM air_pollution WHERE indicator = ?1", nativeQuery = true)
    void deleteAllInBatchByIndicator(String indicator);
}
