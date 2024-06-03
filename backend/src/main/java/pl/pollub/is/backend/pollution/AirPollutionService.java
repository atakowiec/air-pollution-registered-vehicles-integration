package pl.pollub.is.backend.pollution;

import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AirPollutionService {
    private final static List<String> ALLOWED_INDICATORS = List.of("SO2", "NO2", "PM2,5", "Pb(PM10)", "NOx");
    private final static List<String> COLUMN_NAMES = List.of("Rok", "Województwo", "Kod strefy", "Kod stacji", "Wskaźnik", "Czas uśredniania", "Średnia", "Liczba pomiarów");

    private final AirPollutionRepository airPollutionRepository;
    private final DatabaseCacheService cacheService;

    public Map<String, Integer> handleFileUpload(MultipartFile multipartFile) throws IOException {
        Map<String, Integer> savedIndicators = new HashMap<>();

        try (ReadableWorkbook wb = new ReadableWorkbook(multipartFile.getInputStream())) {
            wb.getSheets().forEach(sheet -> processSheet(sheet, savedIndicators));
        }

        return savedIndicators;
    }

    private void processSheet(Sheet sheet, Map<String, Integer> savedIndicators) {
        if (!ALLOWED_INDICATORS.contains(sheet.getName()))
            return;

        airPollutionRepository.deleteAllInBatchByIndicator(sheet.getName());
        cacheService.onDependencyChange(CacheDependency.POLLUTION_DATA);
        try (Stream<Row> rowStream = sheet.openStream()) {
            Map<String, Integer> columnIndexes = new HashMap<>();
            List<AirPollution> toSave = new ArrayList<>();

            rowStream.forEach(row -> {
                // find column indexes for each column name
                if (row.getRowNum() == 1) {
                    for (int i = 0; i < row.getCellCount(); i++) {
                        String cellValue = row.getCellAsString(i).orElseThrow().strip();
                        if (COLUMN_NAMES.contains(cellValue)) {
                            columnIndexes.put(cellValue, i);
                        }
                    }
                    return;
                }

                // process data but only when column with year is present and the value in row is numeric
                String year = getCellValue(row, columnIndexes.get("Rok"), String.class);
                if (year == null || !year.matches("\\d+"))
                    return;

                AirPollution airPollution = new AirPollution();
                airPollution.setYear(Integer.parseInt(year));
                airPollution.setVoivodeship(getCellValue(row, columnIndexes.get("Województwo"), String.class));
                airPollution.setAreaCode(getCellValue(row, columnIndexes.get("Kod strefy"), String.class));
                airPollution.setStationCode(getCellValue(row, columnIndexes.get("Kod stacji"), String.class));
                airPollution.setIndicator(getCellValue(row, columnIndexes.get("Wskaźnik"), String.class));
                airPollution.setAveragingPeriod(getCellValue(row, columnIndexes.get("Czas uśredniania"), String.class));
                airPollution.setAverage(getCellValue(row, columnIndexes.get("Średnia"), Double.class));
                Integer columnIndex = columnIndexes.get("Liczba pomiarów");
                Integer samples = null;
                if (columnIndex != null) {
                    samples = getCellValue(row, columnIndex, Integer.class);
                }
                if (samples == null) {
                    columnIndex = columnIndexes.get("Liczba ważnych pom");
                    if (columnIndex != null) {
                        samples = getCellValue(row, columnIndex, Integer.class);
                    }
                }
                airPollution.setSamples(samples);

                // save airPollution to database
                toSave.add(airPollution);

                // save indicator to savedIndicators map
                savedIndicators.merge(airPollution.getIndicator(), 1, Integer::sum);
            });

            airPollutionRepository.saveAll(toSave);
            cacheService.onDependencyChange(CacheDependency.POLLUTION_DATA);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T getCellValue(Row row, int index, Class<T> clazz) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;

        try {
            if (clazz == String.class) {
                return clazz.cast(cell.getText());
            } else if (clazz == Integer.class) {
                return clazz.cast(Integer.parseInt(cell.getText()));
            } else if (clazz == Double.class) {
                return clazz.cast(Double.parseDouble(cell.getText()));
            } else {
                throw new IllegalArgumentException("Unsupported class type");
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
