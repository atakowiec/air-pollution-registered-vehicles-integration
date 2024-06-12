package pl.pollub.is.backend.vehicles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.util.SimpleJsonBuilder;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehiclesService {
    private final static String VEHICLES_BY_AREA_CODE_KEY = "VEHICLES_BY_AREA_CODE";
    private final static String REGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY = "REGISTRATIONS_BY_YEAR_AND_VOIVODESHIP";
    private final static String DEREGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY = "DEREGISTRATIONS_BY_YEAR_AND_VOIVODESHIP";
    private final static String DEREGISTRATIONS_BY_AREA_CODE_KEY = "DEREGISTRATIONS_BY_AREA_CODE";
    private final static String FUEL_TYPES_KEY= "FUEL_TYPES";
    private final static String TOP_BRANDS_KEY = "TOP_BRANDS";

    private final VehiclesRepository vehiclesRepository;
    private final DatabaseCacheService cacheService;

    public VehiclesService(VehiclesRepository vehiclesRepository, DatabaseCacheService cacheService) {
        this.vehiclesRepository = vehiclesRepository;
        this.cacheService = cacheService;

        registerSuppliers();
    }

    private void registerSuppliers() {
        cacheService.registerSupplier(VEHICLES_BY_AREA_CODE_KEY, this::fetchVehiclesCountByAreaCode, CacheDependency.VEHICLES_DATA);
        cacheService.registerSupplier(REGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY, this::fetchRegistrationsByAreaCodeAndVoivodeships, CacheDependency.VEHICLES_DATA);
        cacheService.registerSupplier(DEREGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY, this::fetchDeregistrationsByAreaCodeAndVoivodeships, CacheDependency.VEHICLES_DATA);
        cacheService.registerSupplier(DEREGISTRATIONS_BY_AREA_CODE_KEY, this::fetchDeregistrationsByAreaCode, CacheDependency.VEHICLES_DATA);
        cacheService.registerSupplier(FUEL_TYPES_KEY, this::fetchFuelTypes, CacheDependency.VEHICLES_DATA);
        cacheService.registerSupplier(TOP_BRANDS_KEY, this::fetchTop10Brands, CacheDependency.VEHICLES_DATA);
    }

    private String fetchTop10Brands() {
        List<Object[]> dbResult = vehiclesRepository.findTop10MostFrequentBrands();
        SimpleJsonBuilder result = SimpleJsonBuilder.empty();

        for (Object[] objects : dbResult) {
            if (objects[0] != null) {
                result.add(objects[0].toString(), objects[1]);
            }
        }

        return result.toJson();
    }


    private String fetchFuelTypes() {
        List<Object[]> dbResult = vehiclesRepository.countVehiclesByFuelType();
        SimpleJsonBuilder result = SimpleJsonBuilder.empty();

        for (Object[] objects : dbResult) {
            result.add(objects[0].toString(), objects[1]);
        }

        return result.toJson();
    }

    private String fetchVehiclesCountByAreaCode() {
        List<Object[]> dbResult = vehiclesRepository.countVehiclesByAreaCode();
        SimpleJsonBuilder result = SimpleJsonBuilder.empty();

        for (Object[] objects : dbResult) {
            result.add(objects[0].toString(), objects[1]);
        }

        return result.toJson();
    }

    private String fetchDeregistrationsByAreaCode() {
        List<Object[]> dbResult = vehiclesRepository.countDeregistrationsByAreaCode();
        SimpleJsonBuilder result = SimpleJsonBuilder.empty();

        for (Object[] objects : dbResult) {
            result.add(objects[0].toString(), objects[1]);
        }

        return result.toJson();
    }

    public String getVehiclesCountByAreaCode() {
        return cacheService.getValue(VEHICLES_BY_AREA_CODE_KEY);
    }

    public String getRegistrationsByAreaCodeAndVoivodeships() {
        return cacheService.getValue(REGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY);
    }

    public String getDeregistrationsByAreaCodeAndVoivodeships() {
        return cacheService.getValue(DEREGISTRATIONS_BY_YEAR_AND_VOIVODESHIP_KEY);
    }

    public String getDeregistrationsByAreaCode() {
        return cacheService.getValue(DEREGISTRATIONS_BY_AREA_CODE_KEY);
    }

    public List<Vehicle> getVehiclesByAreaCodeAndRegistrationYear(String areaCode, int year) {
        List<Vehicle> vehicles = vehiclesRepository.findVehiclesByAreaCodeAndRegistrationYear(areaCode, year);
        return vehicles;
    }

    public List<Object[]> getVehiclesCountByYear(int year) {
        List<Object[]> result = vehiclesRepository.countVehiclesByYear(year);
        return result;
    }

    public String fetchRegistrationsByAreaCodeAndVoivodeships() {
        List<Object[]> registrationsData = vehiclesRepository.countRegistrationsByYearAndVoivodeship();
        return transformResultToJSON(registrationsData);
    }

    public String fetchDeregistrationsByAreaCodeAndVoivodeships() {
        List<Object[]> deregistrationsData = vehiclesRepository.countDeregistrationsByYearAndVoivodeship();
        return transformResultToJSON(deregistrationsData);
    }

    public String transformResultToJSON(List<Object[]> data) {
        Map<Object, Map<Object, Object>> result = new HashMap<>();

        for (Object[] row : data) {
            // skip if any of the values is null - we don't like them
            if (row[0] == null || row[1] == null || row[2] == null) continue;

            String year = row[0].toString();
            String voivodeship = row[1].toString();
            int count = Integer.parseInt(row[2].toString());

            result.computeIfAbsent(year, _ -> new HashMap<>()).put(voivodeship, count);
        }

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    public String getFuelTypes() {
        return cacheService.getValue(FUEL_TYPES_KEY);
    }

    public Object getTop10Brands() {
        return cacheService.getValue(TOP_BRANDS_KEY);
    }
}
