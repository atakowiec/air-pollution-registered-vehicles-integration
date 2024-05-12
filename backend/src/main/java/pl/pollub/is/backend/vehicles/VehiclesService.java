package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    private final VehiclesRepository vehiclesRepository;

    public void processCsvFile(MultipartFile file) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        List<Vehicles> vehiclesList = new ArrayList<>();


        // Read the first line to get column names
        String[] columnNames = br.readLine().split(",");

        int productionYearIndex = -1;
        int productionMethodIndex = -1;

        // Identify the indices of "rok_produkcji" and "sposob_produkcji" columns
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals("rok_produkcji")) {
                productionYearIndex = i;
            } else if (columnNames[i].equals("sposob_produkcji")) {
                productionMethodIndex = i;
            }
        }

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            // Swap values if "rok_produkcji" and "sposob_produkcji" are identified incorrectly
            if (productionYearIndex != -1 && productionMethodIndex != -1 &&
                    data.length > productionYearIndex && data.length > productionMethodIndex) {
                // Check if the values are numeric, if not, swap them
                if (!isNumeric(data[productionYearIndex]) && isNumeric(data[productionMethodIndex])) {
                    String temp = data[productionYearIndex];
                    data[productionYearIndex] = data[productionMethodIndex];
                    data[productionMethodIndex] = temp;
                }
            }

            Vehicles vehicle = new Vehicles();

            // Map data from CSV to Vehicles entity based on column names
            for (int i = 0; i < columnNames.length; i++) {

                String value = data.length > i ? data[i] : "";
                switch (columnNames[i]) {
                    case "pojazd_id":
                        vehicle.setVehicleId(getValueOrNull(value));
                        break;
                    case "akt_miejsce_rej_wojwe":
                        vehicle.setAreaCode(getValueOrNull(value));
                        break;
                    case "akt_miejsce_rej_powiat":
                        vehicle.setCountyCode(getValueOrNull(value));
                        break;
                    case "marka":
                        vehicle.setBrand(getValueOrNull(value));
                        break;
                    case "model":
                        vehicle.setModel(getValueOrNull(value));
                        break;
                    case "rodzaj":
                        vehicle.setType(getValueOrNull(value));
                        break;
                    case "podrodzaj":
                        vehicle.setSubType(getValueOrNull(value));
                        break;
                    case "rok_produkcji":
                        vehicle.setManufactureYear(getIntegerValueOrNull(value));
                        break;
                    case "sposob_produkcji":
                        vehicle.setManufactureMethod(getValueOrNull(value));
                        break;
                    case "data_pierwszej_rej":
                        vehicle.setFirstRegistrationDate(getDateOrNull(value, new SimpleDateFormat("yyyy-MM-dd")));
                        break;
                    case "pojemnosc_silnika":
                        vehicle.setEngineCapacity(getDoubleValueOrNull(value));
                        break;
                    case "moc_silnika":
                        vehicle.setEnginePower(getDoubleValueOrNull(value));
                        break;
                    case "moc_silnika_hybrydowego":
                        vehicle.setHybridEnginePower(getDoubleValueOrNull(value));
                        break;
                    case "masa_wlasna":
                        vehicle.setCurbWeight(getDoubleValueOrNull(value));
                        break;
                    case "rodzaj_paliwa":
                        vehicle.setFuelType(getValueOrNull(value));
                        break;
                    case "rodzaj_paliwa_alternatywnego":
                        vehicle.setAlternativeFuelType(getValueOrNull(value));
                        break;
                    case "rodzaj_paliwa_alternatywnego2":
                        vehicle.setAlternativeFuelType2(getValueOrNull(value));
                        break;
                    case "sr_zuzycie_pal":
                        vehicle.setAverageFuelConsumption(getDoubleValueOrNull(value));
                        break;
                    case "data_wyrejestrowania":
                        vehicle.setDeregistrationDate(getDateOrNull(value, new SimpleDateFormat("yyyy-MM-dd")));
                        break;
                    case "siedziba_wlasciciela_woj":
                        vehicle.setVehiclesOwnerArea(getValueOrNull(value));
                        break;
                    case "emisja_co2":
                        vehicle.setFuelCo2Emission(getDoubleValueOrNull(value));
                        break;
                    case "emisja_co2_pal_alternatywne1":
                        vehicle.setAlternativeFuelCo2Emission(getDoubleValueOrNull(value));
                        break;
                }
            }

            vehiclesList.add(vehicle);
        }

        br.close();

        // Save vehicles to database
        vehiclesRepository.saveAll(vehiclesList);
    }

    private String getValueOrNull(String value) {
        return value.isEmpty() ? null : value;
    }

    private Integer getIntegerValueOrNull(String value) {
        return value.isEmpty() ? null : Integer.parseInt(value);
    }

    private Double getDoubleValueOrNull(String value) {
        return value.isEmpty() ? null : Double.parseDouble(value);
    }

    private Date getDateOrNull(String value, SimpleDateFormat dateFormat) throws ParseException {
        return value.isEmpty() ? null : dateFormat.parse(value);
    }

}