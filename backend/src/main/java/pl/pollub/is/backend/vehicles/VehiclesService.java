package pl.pollub.is.backend.vehicles;

import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.util.SimpleJsonBuilder;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.Date;
import java.util.List;

@Service
public class VehiclesService {
    private final static String VEHICLES_BY_AREA_CODE_KEY = "VEHICLES_BY_AREA_CODE";

    private final VehiclesRepository vehiclesRepository;
    private final DatabaseCacheService cacheService;

    public VehiclesService(VehiclesRepository vehiclesRepository, DatabaseCacheService cacheService) {
        this.vehiclesRepository = vehiclesRepository;
        this.cacheService = cacheService;

        registerSuppliers();
    }

    private void registerSuppliers() {
        cacheService.registerSupplier(VEHICLES_BY_AREA_CODE_KEY, CacheDependency.VEHICLES_DATA, this::fetchVehiclesCountByAreaCode);
    }

    private String fetchVehiclesCountByAreaCode() {
        List<Object[]> dbResult = vehiclesRepository.countVehiclesByAreaCode();
        SimpleJsonBuilder result = SimpleJsonBuilder.empty();

        for (Object[] objects : dbResult) {
            result.add(objects[0].toString(), objects[1]);
        }

        return result.toJson();
    }

    public String getVehiclesCountByAreaCode() {
        return cacheService.getValue(VEHICLES_BY_AREA_CODE_KEY);
    }

    public List<Vehicle> getVehiclesByAreaCodeAndRegistrationYear(String areaCode, int year) {
        System.out.println("Fetching vehicles for areaCode: " + areaCode + " and year: " + year);
        List<Vehicle> vehicles = vehiclesRepository.findVehiclesByAreaCodeAndRegistrationYear(areaCode, year);
        System.out.println("Found " + vehicles.size() + " vehicles");
        return vehicles;
    }
}
