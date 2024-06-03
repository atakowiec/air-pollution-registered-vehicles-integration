package pl.pollub.is.backend.pollution.calculations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.pollution.AirPollutionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AirPollutionCountService {
    private final static String AIR_POLLUTION_BY_INDICATOR_AND_YEAR_KEY = "AIR_POLLUTION_BY_INDICATOR_AND_YEAR";
    private final AirPollutionRepository airPollutionRepository;
    private final DatabaseCacheService cacheService;

    public AirPollutionCountService(AirPollutionRepository airPollutionRepository, DatabaseCacheService cacheService) {
        this.airPollutionRepository = airPollutionRepository;
        this.cacheService = cacheService;

        cacheService.registerSupplier(AIR_POLLUTION_BY_INDICATOR_AND_YEAR_KEY, CacheDependency.POLLUTION_DATA, this::fetchAverageByYearVoivodeshipAndIndicator);
    }

    public List<Object[]> getAveragePollutionByIndicatorAndYear(String indicator, int year) {
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYear(indicator, year);
        return result;
    }

    public List<Object[]> getAveragePollutionByIndicatorAndYearInVoivodeship(String indicator, int year, String voivodeship) {
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYearInVoivodeship(indicator, year, voivodeship);
        return result;
    }

    public List<Object[]> getAveragePollutionByYear(int year) {
        return airPollutionRepository.findAveragePollutionByYear(year);
    }

    public String getAverageByYearVoivodeshipAndIndicator() {
        return cacheService.getValue(AIR_POLLUTION_BY_INDICATOR_AND_YEAR_KEY);
    }

    public String fetchAverageByYearVoivodeshipAndIndicator() {
        List<Object[]> dbResult = airPollutionRepository.averageByYearVoivodeshipAndIndicator();

        Map<String, Map<String, Map<String, Double>>> result = new HashMap<>();

        for (Object[] row : dbResult) {
            String year = row[0].toString();
            String voivodeship = row[1].toString();
            String indicator = row[2].toString();
            Double average = (Double) row[3];

            result.computeIfAbsent(year, _ -> new HashMap<>())
                    .computeIfAbsent(voivodeship, _ -> new HashMap<>())
                    .put(indicator, average);
        }

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
