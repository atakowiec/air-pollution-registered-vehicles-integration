package pl.pollub.is.backend.vehicles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VehiclesRepository extends JpaRepository<Vehicle, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM vehicles WHERE indicator = ?1", nativeQuery = true)
    void deleteAllInBatchByIndicator(String indicator);
}
