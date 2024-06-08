package pl.pollub.is.backend.general;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.pollution.AirPollutionRepository;
import pl.pollub.is.backend.util.SimpleJsonBuilder;
import pl.pollub.is.backend.vehicles.VehiclesRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneralService {
    private final static String BY_YEAR_AND_VOIVODESHIPS_KEY = "MERGED_DATA_BY_YEAR_AND_VOIVODESHIPS";

    private final DatabaseCacheService cacheService;
    private final VehiclesRepository vehiclesRepository;
    private final AirPollutionRepository airPollutionRepository;

    @PostConstruct
    public void registerCache() {
        cacheService.registerSupplier(BY_YEAR_AND_VOIVODESHIPS_KEY, this::fetchDataByYearAndVoivodeships, CacheDependency.POLLUTION_DATA, CacheDependency.VEHICLES_DATA);
    }

    public String fetchDataByYearAndVoivodeships() {
        List<Object[]> registrationsResult = vehiclesRepository.countRegistrationsByYearAndVoivodeship();
        List<Object[]> deregistrationsResult = vehiclesRepository.countDeregistrationsByYearAndVoivodeship();
        List<Object[]> pollutionResult = airPollutionRepository.averageByYearVoivodeshipAndIndicator();

        Map<String, Map<String, Map<String, Number>>> result = new HashMap<>();

        for (Object[] objects : registrationsResult) {
            String year = objects[0].toString();
            String voivodeship = objects[1].toString().toLowerCase();
            Number count = (Number) objects[2];

            result.computeIfAbsent(year, _ -> new HashMap<>())
                    .computeIfAbsent(voivodeship, _ -> new HashMap<>())
                    .put("registrations", count);
        }

        for (Object[] objects : deregistrationsResult) {
            String year = objects[0].toString();
            String voivodeship = objects[1].toString().toLowerCase();
            Number count = (Number) objects[2];

            result.computeIfAbsent(year, _ -> new HashMap<>())
                    .computeIfAbsent(voivodeship, _ -> new HashMap<>())
                    .put("deregistrations", count);
        }

        for (Object[] objects : pollutionResult) {
            String year =  objects[0].toString();
            String voivodeship = objects[1].toString().toLowerCase();
            String indicator = objects[2].toString();
            Number value = (Number) objects[3];

            result.computeIfAbsent(year, _ -> new HashMap<>())
                    .computeIfAbsent(voivodeship, _ -> new HashMap<>())
                    .put(indicator, value);
        }

        return SimpleJsonBuilder.of(result).toJson();
    }

    public String getDataByYearAndVoivodeships() {
        return cacheService.getValue(BY_YEAR_AND_VOIVODESHIPS_KEY);
    }
}
