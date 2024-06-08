package pl.pollub.is.backend.general;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.pollution.AirPollutionRepository;
import pl.pollub.is.backend.util.SimpleJsonBuilder;
import pl.pollub.is.backend.vehicles.VehiclesRepository;

import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneralService {
    private final ObjectMapper objectMapper = new ObjectMapper();
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
            String year = objects[0].toString();
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

    public Map<String, Map<String, Map<String, Number>>> prepareData(int startYear, int endYear, String voivodeships, String indicators) {
        Map<String, Map<String, Map<String, Number>>> data = null;
        try {
            //noinspection unchecked
            data = objectMapper.readValue(getDataByYearAndVoivodeships(), Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }

        if (startYear != -1 || endYear != -1) {
            int finalStartYear = startYear == -1 ? Integer.MIN_VALUE : startYear;
            int finalEndYear = endYear == -1 ? Integer.MAX_VALUE : endYear;

            data.entrySet().removeIf(entry -> {
                int year = Integer.parseInt(entry.getKey());
                return year < finalStartYear || year > finalEndYear;
            });
        }

        List<String> allowedVoivodeships = List.of(voivodeships.split(","));
        if (!voivodeships.equals("*")) {
            data.forEach((_, voivodeshipData) -> voivodeshipData.keySet().retainAll(allowedVoivodeships));
        }

        List<String> allowedIndicators = List.of(indicators.split(","));
        if (!indicators.equals("*")) {
            data.forEach((_, voivodeshipData) -> voivodeshipData.forEach((_, indicatorData) -> indicatorData.keySet().retainAll(allowedIndicators)));
        }

        // remove voivodeships with no data
        data.forEach((year, voivodeshipData) -> voivodeshipData.entrySet().removeIf(entry -> entry.getValue().isEmpty()));

        // remove years with no data
        data.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return data;
    }

    public ByteArrayOutputStream exportDataAsJson(int startYear, int endYear, String voivodeships, String indicators) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // first we need to create some metadata
        SimpleJsonBuilder metadataBuilder = SimpleJsonBuilder.empty();
        metadataBuilder.add("exported_at", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
        metadataBuilder.add("title", "Data grouped by year and voivodeships");
        metadataBuilder.add("description", "Data about vehicle registrations, deregistrations and air pollution by year and voivodeship. Air pollution data is presented as average values of each indicator.");
        if(startYear != -1) metadataBuilder.add("start_year", startYear);
        if(endYear != -1) metadataBuilder.add("end_year", endYear);
        metadataBuilder.add("selected_voivodeships", voivodeships.split(","));
        metadataBuilder.add("selected_indicators", indicators.split(","));

        // then we need to create the json structure with metadata and data
        SimpleJsonBuilder jsonBuilder = SimpleJsonBuilder.empty();
        jsonBuilder.add("metadata", metadataBuilder.build());
        jsonBuilder.add("data", prepareData(startYear, endYear, voivodeships, indicators));

        try {
            byteArrayOutputStream.write(jsonBuilder.toJson().getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream;
    }

    public ByteArrayOutputStream exportDataAsCsv(int startYear, int endYear, String voivodeships, String indicators) {
        return null; // todo
    }

    public ByteArrayOutputStream exportDataAsXml(int startYear, int endYear, String voivodeships, String indicators) {
        return null; // todo
    }

    public ByteArrayOutputStream exportDataAsYaml(int startYear, int endYear, String voivodeships, String indicators) {
        return null; // todo
    }

    public ByteArrayOutputStream exportDataAsXlsx(int startYear, int endYear, String voivodeships, String indicators) {
        return null; // todo
    }
}
