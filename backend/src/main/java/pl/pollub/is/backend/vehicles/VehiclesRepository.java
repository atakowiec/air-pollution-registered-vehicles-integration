package pl.pollub.is.backend.vehicles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.List;

public interface VehiclesRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    @Query(value = "SELECT v.areaCode, COUNT(v) FROM Vehicle v WHERE v.fuelType is not null GROUP BY v.areaCode")
    List<Object[]> countVehiclesByAreaCode();

    @Query(value = "SELECT * FROM vehicles WHERE fuel_type is not null " +
            "AND area_code = :areaCode " +
            "AND first_registration_date <= CONCAT(:year, '-12-31') " +
            "AND (deregistration_date IS NULL OR deregistration_date >= CONCAT(:year, '-01-01'))",
            nativeQuery = true)
    List<Vehicle> findVehiclesByAreaCodeAndRegistrationYear(
            @Param("areaCode") String areaCode,
            @Param("year") int year
    );

    @Query(value = "SELECT area_code, COUNT(*) FROM vehicles " +
            "WHERE first_registration_date <= CONCAT(:year, '-12-31') " +
            "AND v.fuelType is not null " +
            "AND (deregistration_date IS NULL OR deregistration_date >= CONCAT(:year, '-01-01')) " +
            "GROUP BY area_code",
            nativeQuery = true)
    List<Object[]> countVehiclesByYear(@Param("year") int year);

    @Query(value = "SELECT year(first_registration_date) as year, area_code, count(*) FROM `vehicles`" +
            "WHERE first_registration_date IS NOT NULL " +
            "AND year(first_registration_date) BETWEEN 1900 AND 2019 " +
            "AND fuel_type is not null " +
            "GROUP BY year(first_registration_date), area_code",
            nativeQuery = true)
    List<Object[]> countRegistrationsByYearAndVoivodeship();

    @Query(value = "SELECT year(deregistration_date) as year, area_code, count(*) FROM `vehicles` " +
            "WHERE deregistration_date is not null " +
            "AND year(deregistration_date) BETWEEN 1900 AND 2019 " +
            "AND fuel_type is not null " +
            "GROUP BY year(deregistration_date), area_code;",
            nativeQuery = true)
    List<Object[]> countDeregistrationsByYearAndVoivodeship();

    @Query(value = "SELECT v.areaCode, COUNT(*) FROM Vehicle v WHERE v.deregistrationDate IS NOT NULL AND v.fuelType is not null GROUP BY v.areaCode")
    List<Object[]> countDeregistrationsByAreaCode();

    @Query(value = "SELECT v.fuelType, COUNT(v) FROM Vehicle v WHERE v.fuelType IS NOT NULL AND v.fuelType != 'NIE DOTYCZY' GROUP BY v.fuelType")
    List<Object[]> countVehiclesByFuelType();

    @Query(value = "SELECT v.brand, COUNT(v) as count FROM Vehicle v GROUP BY v.brand ORDER BY count DESC LIMIT 10")
    List<Object[]> findTop10MostFrequentBrands();


}
