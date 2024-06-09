package pl.pollub.is.backend.vehicles.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.vehicles.VehiclesRepository;
import pl.pollub.is.backend.vehicles.model.Vehicle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Vehicles API Service")
public class VehiclesServiceApi {
    private final VehiclesRepository vehiclesRepository;
    private final DatabaseCacheService cacheService;

    public void processJsonFromUrl(String jsonString) throws IOException, ParseException {
        log.info("Started processing vehicles data from API.");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode apiUrlNode = rootNode.get("apiUrl");
        if (apiUrlNode != null) {
            String apiUrl = apiUrlNode.asText();
            ObjectMapper objectMapper2 = new ObjectMapper();
            log.info("Fetching data from API: {}", apiUrl);
            URL url = new URL(apiUrl);
            String jsonContent = stream(url);
            JsonNode apiUrlData = objectMapper2.readTree(jsonContent);
            JsonNode dataNode = apiUrlData.get("data");
            //if we pass api with single object
            if (dataNode.isObject())
            {
                processSingleVehicle(dataNode);
            }
            //if we pass api with array of objects
            else if (dataNode.isArray()) {
                processVehicleArray(dataNode);
            }
        }
        else {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Invalid JSON format. Missing 'apiUrl' field.");
        }

        cacheService.onDependencyChange(CacheDependency.VEHICLES_DATA);
    }

    private void processVehicleArray(JsonNode dataNode) throws ParseException {
        for (JsonNode vehicleNode : dataNode) {
            JsonNode attributesNode = vehicleNode.get("attributes");
            Vehicle vehicle = createVehicleFromJson(vehicleNode, attributesNode);
            vehiclesRepository.save(vehicle);
        }

        log.info("{} vehicles have been saved to the database.", dataNode.size());
    }

    private void processSingleVehicle(JsonNode vehicleNode) throws ParseException {
        JsonNode attributesNode = vehicleNode.get("attributes");
        Vehicle vehicle = createVehicleFromJson(vehicleNode,attributesNode);
        vehiclesRepository.save(vehicle);

        log.info("Vehicle with ID {} has been saved to the database.", vehicle.getVehicleId());
    }

    public static String stream(URL url) throws IOException {
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            return json.toString();
        }
    }

    private Vehicle createVehicleFromJson(JsonNode dataNode,JsonNode attributesNode) throws ParseException {
        Vehicle vehicle = new Vehicle();

        String id = getValueOrNull(dataNode, "id");
        vehicle.setVehicleId(id != null ? new BigInteger(id) : null);
        vehicle.setAreaCode(getValueOrNull(attributesNode, "rejestracja-wojewodztwo"));
        vehicle.setCountyCode(getValueOrNull(attributesNode, "rejestracja-powiat"));
        vehicle.setBrand(getValueOrNull(attributesNode, "marka"));
        vehicle.setModel(getValueOrNull(attributesNode, "model"));
        vehicle.setType(getValueOrNull(attributesNode, "rodzaj-pojazdu"));
        vehicle.setSubType(getValueOrNull(attributesNode, "podrodzaj-pojazdu"));

        String productionYear = getValueOrNull(attributesNode, "rok-produkcji");
        String productionMethod = getValueOrNull(attributesNode, "sposob-produkcji");

        // Check if both production year and method are not null
        if (productionYear != null && productionMethod != null) {
            // Check if production year is not numeric but production method is
            if (!isNumeric(productionYear) && isNumeric(productionMethod)) {
                // Swap values
                String temp = productionYear;
                productionYear = productionMethod;
                productionMethod = temp;
            }
            vehicle.setManufactureYear(Integer.parseInt(productionYear));
            vehicle.setManufactureMethod(productionMethod);
        }
        else if (productionYear == null && productionMethod != null) {
            // If production year is null but production method is not, swap them
            if(isNumeric(productionMethod)) {
                productionYear = productionMethod;
                productionMethod = null;
            }
            vehicle.setManufactureYear(Integer.parseInt(productionYear));
            vehicle.setManufactureMethod(null);
        }
        else if (productionYear != null) {
            if(!isNumeric(productionYear)) {
                productionMethod = productionYear;
                productionYear = null;
            }
            vehicle.setManufactureYear(null);
            vehicle.setManufactureMethod(productionMethod);
        }

        vehicle.setFirstRegistrationDate(getDateOrNull(attributesNode, "data-pierwszej-rejestracji-w-kraju"));
        vehicle.setEngineCapacity(getDoubleValueOrNull(attributesNode, "pojemnosc-skokowa-silnika"));
        vehicle.setEnginePower(getDoubleValueOrNull(attributesNode, "moc-netto-silnika"));
        vehicle.setHybridEnginePower(getDoubleValueOrNull(attributesNode, "moc-netto-silnika-hybrydowego"));
        vehicle.setCurbWeight(getDoubleValueOrNull(attributesNode, "masa-wlasna"));
        vehicle.setFuelType(getValueOrNull(attributesNode, "rodzaj-paliwa"));
        vehicle.setAlternativeFuelType(getValueOrNull(attributesNode, "rodzaj-pierwszego-paliwa-alternatywnego"));
        vehicle.setAlternativeFuelType2(getValueOrNull(attributesNode, "rodzaj-drugiego-paliwa-alternatywnego"));
        vehicle.setAverageFuelConsumption(getDoubleValueOrNull(attributesNode, "srednie-zuzycie-paliwa"));
        vehicle.setDeregistrationDate(getDateOrNull(attributesNode, "data-wyrejestrowania-pojazdu"));
        vehicle.setVehiclesOwnerArea(getValueOrNull(attributesNode, "wlasciciel-wojewodztwo"));
        vehicle.setFuelCo2Emission(getDoubleValueOrNull(attributesNode, "poziom-emisji-co2"));
        vehicle.setAlternativeFuelCo2Emission(getDoubleValueOrNull(attributesNode, "poziom-emisji-co2-paliwo-alternatywne-1"));

        return vehicle;
    }

    private String getValueOrNull(JsonNode node, String fieldName) {
        if(node == null){
            return null;
        }
        else {
            JsonNode valueNode = node.get(fieldName);
            return (valueNode != null && !valueNode.isNull()) ? valueNode.asText() : null;
        }
    }

    private Integer getIntegerValueOrNull(JsonNode node, String fieldName) {
        if(node == null){
            return null;
        }
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asInt() : null;
    }

    private Double getDoubleValueOrNull(JsonNode node, String fieldName) {
        if(node == null){
            return null;
        }
        JsonNode valueNode = node.get(fieldName);
        return (valueNode != null && !valueNode.isNull()) ? valueNode.asDouble() : null;
    }

    private Date getDateOrNull(JsonNode node, String fieldName) throws ParseException {
        if(node == null){
            return null;
        }
        JsonNode valueNode = node.get(fieldName);
        if (valueNode != null && !valueNode.isNull()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(valueNode.asText());
        } else {
            return null;
        }
    }


}
