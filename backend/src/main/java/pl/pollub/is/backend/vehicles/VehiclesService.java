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

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            Vehicles vehicle = new Vehicles();

            // Map data from CSV to Vehicles entity based on column names
            for (int i = 0; i < columnNames.length; i++) {
                switch (columnNames[i]) {
                    case"akt_miejsce_rej_wojwe":
                        vehicle.setAreaCode(getValueOrNull(data[i]));
                        break;
                    case "akt_miejsce_rej_powiat":
                        vehicle.setCountyCode(getValueOrNull(data[i]));
                        break;
                    case "marka":
                        vehicle.setBrand(getValueOrNull(data[i]));
                        break;
                    case "model":
                        vehicle.setModel(getValueOrNull(data[i]));
                        break;
                    case "rodzaj":
                        vehicle.setType(getValueOrNull(data[i]));
                        break;
                    case "podrodzaj":
                        vehicle.setSubType(getValueOrNull(data[i]));
                        break;
                    case "rok_produkcji":
                        vehicle.setManufactureYear(getIntegerValueOrNull(data[i]));
                        break;
                    case "sposob_produkcji":
                        vehicle.setManufactureMethod(getValueOrNull(data[i]));
                        break;
                    case "data_pierwszej_rej":
                        vehicle.setFirstRegistrationDate(getDateOrNull(data[i], new SimpleDateFormat("yyyy-MM-dd")));
                        break;
                    case "pojemnosc_silnika":
                        vehicle.setEngineCapacity(getDoubleValueOrNull(data[i]));
                        break;
                    case "moc_silnika":
                        vehicle.setEnginePower(getDoubleValueOrNull(data[i]));
                        break;
                    case "moc_silnika_hybrydowego":
                        vehicle.setHybridEnginePower(getDoubleValueOrNull(data[i]));
                        break;
                    case "masa_wlasna":
                        vehicle.setCurbWeight(getDoubleValueOrNull(data[i]));
                        break;
                    case "rodzaj_paliwa":
                        vehicle.setFuelType(getValueOrNull(data[i]));
                        break;
                    case "rodzaj_paliwa_alternatywnego":
                        vehicle.setAlternativeFuelType(getValueOrNull(data[i]));
                        break;
                    case "rodzaj_paliwa_alternatywnego2":
                        vehicle.setAlternativeFuelType2(getValueOrNull(data[i]));
                        break;
                    case "sr_zuzycie_pal":
                        vehicle.setAverageFuelConsumption(getDoubleValueOrNull(data[i]));
                        break;
                    case "data_wyrejestrowania":
                        vehicle.setDeregistrationDate(getDateOrNull(data[i], new SimpleDateFormat("yyyy-MM-dd")));
                        break;
                    case "siedziba_wlasciciela_woj":
                        vehicle.setVehiclesOwnerArea(getValueOrNull(data[i]));
                        break;
//                    case "emisja_co2":
//                        vehicle.setFuelCo2Emission(getDoubleValueOrNull(data[i]));
//                        break;
//                    case "emisja_co2_pal_alternatywne1":
//                        vehicle.setAlternativeFuelCo2Emission(getDoubleValueOrNull(data[i]));
//                        break;
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