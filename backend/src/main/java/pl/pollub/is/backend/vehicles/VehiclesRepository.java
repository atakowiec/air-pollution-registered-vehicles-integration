package pl.pollub.is.backend.vehicles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.Date;
import java.util.List;

public interface VehiclesRepository extends JpaRepository<Vehicle, Integer> {

    @Query("SELECT v.areaCode, COUNT(v) FROM Vehicle v GROUP BY v.areaCode")
    List<Object[]> countVehiclesByAreaCode();

    @Query(value = "SELECT * FROM vehicles WHERE area_code = :areaCode AND manufacture_year = :year " +
            "AND first_registration_date <= CONCAT(:year, '-12-31') " +
            "AND (deregistration_date IS NULL OR deregistration_date >= CONCAT(:year, '-01-01'))",
            nativeQuery = true)
    List<Vehicle> findVehiclesByAreaCodeAndRegistrationYear(
            @Param("areaCode") String areaCode,
            @Param("year") int year
    );

}
