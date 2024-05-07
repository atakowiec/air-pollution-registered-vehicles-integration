package pl.pollub.is.backend.vehicles;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private VehiclesRepository vehiclesRepository;

    public void processCsvFile(MultipartFile file) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        List<Vehicles> vehiclesList = new ArrayList<>();

        // Skip the first line (header)
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");

            Vehicles vehicle = new Vehicles();

            // Map data from CSV to Vehicles entity
            vehicle.setAreaCode(data[58]);//
            vehicle.setCountyCode(data[59]);//
            vehicle.setBrand(data[1]);//
            vehicle.setModel(data[4]);//
            vehicle.setType(data[7]);//
            vehicle.setSubType(data[8]);//
            vehicle.setManufactureYear(Integer.parseInt(data[12]));//
            vehicle.setManufactureMethod(data[13]);//

            // Parse date strings
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            vehicle.setFirstRegistrationDate(dateFormat.parse(data[14]));//
            vehicle.setDeregistrationDate(dateFormat.parse(data[55]));//

            vehicle.setEngineCapacity(Double.parseDouble(data[17]));//
            vehicle.setEnginePower(Double.parseDouble(data[19]));//
            vehicle.setHybridEnginePower(Double.parseDouble(data[20]));//
            vehicle.setCurbWeight(Double.parseDouble(data[21]));//
            vehicle.setFuelType(data[36]);//
            vehicle.setAlternativeFuelType(data[37]);//
            vehicle.setAlternativeFuelType2(data[38]);//
            vehicle.setAverageFuelConsumption(Double.parseDouble(data[39]));//
            vehicle.setVehiclesOwnerArea(data[61]);//
            vehicle.setFuelCo2Emission(Double.parseDouble(data[69]));//
            vehicle.setAlternativeFuelCo2Emission(Double.parseDouble(data[70]));//

            vehiclesList.add(vehicle);
        }

        br.close();

        // Save vehicles to database
        vehiclesRepository.saveAll(vehiclesList);
    }
}
