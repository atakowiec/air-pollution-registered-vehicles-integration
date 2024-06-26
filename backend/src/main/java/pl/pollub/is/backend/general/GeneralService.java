package pl.pollub.is.backend.general;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.pollution.AirPollutionRepository;
import pl.pollub.is.backend.util.SimpleJsonBuilder;
import pl.pollub.is.backend.vehicles.VehiclesRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeneralService {
    private final static String BY_YEAR_AND_VOIVODESHIPS_KEY = "MERGED_DATA_BY_YEAR_AND_VOIVODESHIPS";
    private final static String[] INDICATORS = {"registrations", "deregistrations", "SO2", "NO2", "PM2.5", "Pb(PM10)", "NOx"};
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        Map<String, Map<String, Map<String, Number>>> data;
        try {
            //noinspection unchecked
            data = objectMapper.readValue(getDataByYearAndVoivodeships(), Map.class);
        } catch (JsonProcessingException e) {
            throw new HttpException(500, "Internal server error while parsing data.");
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
        data.forEach((_, voivodeshipData) -> voivodeshipData.entrySet().removeIf(entry -> entry.getValue().isEmpty()));

        // remove years with no data
        data.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        return data;
    }

    public ByteArrayOutputStream exportDataAsJson(int startYear, int endYear, String voivodeships, String indicators) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // first we need to create some metadata
        SimpleJsonBuilder metadataBuilder = SimpleJsonBuilder.empty();
        metadataBuilder.add("exported_at", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        metadataBuilder.add("title", "Data grouped by year and voivodeships");
        metadataBuilder.add("description", "Data about vehicle registrations, deregistrations and air pollution by year and voivodeship. Air pollution data is presented as average values of each indicator.");
        if (startYear != -1) metadataBuilder.add("start_year", startYear);
        if (endYear != -1) metadataBuilder.add("end_year", endYear);
        metadataBuilder.add("selected_voivodeships", voivodeships.split(","));
        metadataBuilder.add("selected_indicators", indicators.split(","));

        // then we need to create the json structure with metadata and data
        SimpleJsonBuilder jsonBuilder = SimpleJsonBuilder.empty();
        jsonBuilder.add("metadata", metadataBuilder.build());
        jsonBuilder.add("data", prepareData(startYear, endYear, voivodeships, indicators));

        try {
            byteArrayOutputStream.write(jsonBuilder.toJson().getBytes());
        } catch (Exception e) {
            throw new HttpException(500, "Internal server error while exporting data.");
        }

        return byteArrayOutputStream;
    }

    public ByteArrayOutputStream exportDataAsCsv(int startYear, int endYear, String voivodeships, String indicators) {
        Map<String, Map<String, Map<String, Number>>> data = prepareData(startYear, endYear, voivodeships, indicators);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String[] selectedIndicators = indicators.equals("*") ? INDICATORS : indicators.split(",");

        String[] headers = new String[selectedIndicators.length + 2];
        headers[0] = "year";
        headers[1] = "voivodeship";
        System.arraycopy(selectedIndicators, 0, headers, 2, selectedIndicators.length);

        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader(headers).build();

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8), format)) {
            List<String> keys = data.keySet().stream().sorted().toList();

            for (String year : keys) {
                Map<String, Map<String, Number>> value = data.get(year);

                for (Map.Entry<String, Map<String, Number>> voivodeshipEntry : value.entrySet()) {
                    String voivodeship = voivodeshipEntry.getKey();
                    List<String> rowValues = new ArrayList<>();
                    rowValues.add(year);
                    rowValues.add(voivodeship);

                    for (String indicator : selectedIndicators) {
                        Number indicatorValue = voivodeshipEntry.getValue().get(indicator);
                        rowValues.add(indicatorValue == null ? "" : indicatorValue.toString());
                    }

                    csvPrinter.printRecord(rowValues);
                }
            }
        } catch (IOException e) {
            throw new HttpException(500, "Internal server error while exporting data.");
        }

        return byteArrayOutputStream;
    }

    public ByteArrayOutputStream exportDataAsXml(int startYear, int endYear, String voivodeships, String indicators) {
        Map<String, Map<String, Map<String, Number>>> data = prepareData(startYear, endYear, voivodeships, indicators);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("root");
            document.appendChild(rootElement);

            // metadata
            xmlMetadata(startYear, endYear, voivodeships, indicators, document, rootElement);

            Element dataElement = document.createElement("data");
            rootElement.appendChild(dataElement);

            for (Map.Entry<String, Map<String, Map<String, Number>>> yearEntry : data.entrySet()) {
                Element yearElement = document.createElement("year");
                yearElement.setAttribute("value", yearEntry.getKey());
                dataElement.appendChild(yearElement);

                for (Map.Entry<String, Map<String, Number>> voivodeshipEntry : yearEntry.getValue().entrySet()) {
                    Element voivodeshipElement = document.createElement("voivodeship");
                    voivodeshipElement.setAttribute("name", voivodeshipEntry.getKey());
                    yearElement.appendChild(voivodeshipElement);

                    for (Map.Entry<String, Number> indicatorEntry : voivodeshipEntry.getValue().entrySet()) {
                        Element indicatorElement = document.createElement("indicator");
                        indicatorElement.setAttribute("name", indicatorEntry.getKey());
                        indicatorElement.setTextContent(indicatorEntry.getValue().toString());
                        voivodeshipElement.appendChild(indicatorElement);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);

            transformer.transform(domSource, new StreamResult(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new HttpException(500, "Internal server error while exporting data.");
        }

        return byteArrayOutputStream;
    }

    private void xmlMetadata(int startYear, int endYear, String voivodeships, String indicators, Document document, Element rootElement) {
        Element metadataElement = document.createElement("metadata");
        rootElement.appendChild(metadataElement);

        Element exportedAtElement = document.createElement("exported_at");
        exportedAtElement.setTextContent(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        metadataElement.appendChild(exportedAtElement);

        Element titleElement = document.createElement("title");
        titleElement.setTextContent("Data grouped by year and voivodeships");
        metadataElement.appendChild(titleElement);

        Element descriptionElement = document.createElement("description");
        descriptionElement.setTextContent("Data about vehicle registrations, deregistrations and air pollution by year and voivodeship. Air pollution data is presented as average values of each indicator.");
        metadataElement.appendChild(descriptionElement);

        if (startYear != -1) {
            Element startYearElement = document.createElement("start_year");
            startYearElement.setTextContent(String.valueOf(startYear));
            metadataElement.appendChild(startYearElement);
        }

        if (endYear != -1) {
            Element endYearElement = document.createElement("end_year");
            endYearElement.setTextContent(String.valueOf(endYear));
            metadataElement.appendChild(endYearElement);
        }

        Element selectedVoivodeshipsElement = document.createElement("selected_voivodeships");
        metadataElement.appendChild(selectedVoivodeshipsElement);
        for (String voivodeship : voivodeships.split(",")) {
            Element voivodeshipElement = document.createElement("voivodeship");
            voivodeshipElement.setTextContent(voivodeship);
            selectedVoivodeshipsElement.appendChild(voivodeshipElement);
        }

        Element selectedIndicatorsElement = document.createElement("selected_indicators");
        metadataElement.appendChild(selectedIndicatorsElement);
        for (String indicator : indicators.split(",")) {
            Element indicatorElement = document.createElement("indicator");
            indicatorElement.setTextContent(indicator);
            selectedIndicatorsElement.appendChild(indicatorElement);
        }
    }
}
