package pl.pollub.is.backend.vehicles;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.progress.ProgressService;
import pl.pollub.is.backend.progress.model.ProgressStatus;
import pl.pollub.is.backend.util.FileUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.microsoft.sqlserver.jdbc.StringUtils.isNumeric;

@Service
public class VehiclesService {
    private final ThreadPoolTaskExecutor saveExecutor = saveAsyncExecutor();
    private final VehiclesRepository vehiclesRepository;
    private final ThreadPoolTaskExecutor asyncExecutor;
    private final VehiclesImportProgress progress;

    public VehiclesService(VehiclesRepository vehiclesRepository, ProgressService progressService, ThreadPoolTaskExecutor asyncExecutor) {
        this.vehiclesRepository = vehiclesRepository;
        this.asyncExecutor = asyncExecutor;
        this.progress = progressService.registerProgress(new VehiclesImportProgress());
    }

    public ResponseEntity<String> processCsvFile(MultipartFile multipartFile) throws IOException, ParseException {
        // do not allow to start the import when another import is in progress
        if (progress.getStatus() != ProgressStatus.NOT_STARTED && progress.getStatus() != ProgressStatus.FINISHED)
            throw new HttpException(HttpStatus.CONFLICT, "Operation already in progress");

        // clone multipartFile because spring deletes uploaded files as soon as the request is handled
        File file = FileUtil.multipartToFile(multipartFile);

        // clear progress data from previous operation
        progress.clear();
        progress.setStartDate();
        progress.setStatus(ProgressStatus.IN_PROGRESS);

        // run method asynchronously through executor
        // cannot use @Async because method is called from the same class
        asyncExecutor.execute(() -> {
            try {
                processCsvFileAsync(file);
            } catch (Exception e) {
                progress.setStatus(ProgressStatus.FAILED);
                progress.setEndDate();
                throw new RuntimeException(e);
            }
        });

        // client will receive response with initial progress data as soon as the request is handled
        return progress.toResponseEntity();
    }

    public void processCsvFileAsync(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long count = br.lines().count() - 1;
        long read = 0;
        br = new BufferedReader(new FileReader(file));
        // Read the first line to get column names
        String[] columnNames = br.readLine().split(",");

        // Identify the indices of "rok_produkcji" and "sposob_produkcji" columns
        int productionYearIndex = -1;
        int productionMethodIndex = -1;

        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals("rok_produkcji")) {
                productionYearIndex = i;
            } else if (columnNames[i].equals("sposob_produkcji")) {
                productionMethodIndex = i;
            }
        }

        progress.setTotal(count);
        progress.setDataLoaded(true);

        List<Vehicles> vehiclesList = new LinkedList<>();
        String line;
        while ((line = br.readLine()) != null) {
            List<String> data = splitLine(line);

            try {
                processRow(data, columnNames, vehiclesList, productionMethodIndex, productionYearIndex);

                read++;
                progress.setRead(read);

                vehiclesList = handleSave(vehiclesList);
            } catch (Exception e) {
                Map<String, String> columnData = new HashMap<>();
                for (int i = 0; i < columnNames.length; i++) {
                    columnData.put(columnNames[i], data.size() > i ? data.get(i) : "");
                }

                progress.addReadError(data.getFirst(), e.getLocalizedMessage(), columnData, line);
                e.printStackTrace();
            }
        }

        progress.setReadDate();

        br.close();
        // Save vehicles to database
        saveVehiclesAsync(vehiclesList);
    }

    private List<Vehicles> handleSave(List<Vehicles> vehiclesList) {
        if (vehiclesList.size() < 5000)
            return vehiclesList;

        saveVehiclesAsync(vehiclesList);

        return new LinkedList<>();
    }

    private void processRow(List<String> data, String[] columnNames, List<Vehicles> vehiclesList, int productionMethodIndex, int productionYearIndex) throws ParseException {
        // Swap values if "rok_produkcji" and "sposob_produkcji" are identified incorrectly
        if (productionYearIndex != -1 &&
                productionMethodIndex != -1 &&
                data.size() > productionYearIndex &&
                data.size() > productionMethodIndex) {

            // Check if the values are numeric, if not, swap them
            if (!isNumeric(data.get(productionYearIndex)) && isNumeric(data.get(productionMethodIndex))) {
                String temp = data.get(productionYearIndex);
                data.set(productionYearIndex, data.get(productionMethodIndex));
                data.set(productionMethodIndex, temp);
            }
        }

        Vehicles vehicle = new Vehicles();

        // Map data from CSV to Vehicles entity based on column names
        for (int i = 0; i < columnNames.length; i++) {
            String value = data.size() > i ? data.get(i) : "";

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

    private void saveVehiclesAsync(List<Vehicles> toSave) {
        saveExecutor.execute(() -> {
            try {
                // todo change save to be able to track errors (which vehicles were not saved and why)
                vehiclesRepository.saveAll(toSave);
            } catch (Exception e) {
                progress.addSaveError(e.getMessage());
                e.printStackTrace();
            }
            progress.addSaved(toSave.size());

            if (progress.getSaved() + progress.getReadErrors() >= progress.getTotal()) {
                progress.setStatus(ProgressStatus.FINISHED);
                progress.setEndDate();
            }
        });
    }

    public ThreadPoolTaskExecutor saveAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(6);
        executor.setThreadNamePrefix("SaveThread-");
        executor.initialize();
        return executor;
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

    private List<String> splitLine(String line) {
        List<String> values = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }

        values.add(sb.toString());

        return values;
    }
}