package pl.pollub.is.backend.vehicles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehiclesRepository extends JpaRepository<Vehicle, Integer> {

    @Query("SELECT v.areaCode, COUNT(v) FROM Vehicle v GROUP BY v.areaCode")
    List<Object[]> countVehiclesByAreaCode();
}
