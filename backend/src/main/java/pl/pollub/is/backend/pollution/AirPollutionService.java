package pl.pollub.is.backend.pollution;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.pollub.is.backend.cache.DatabaseCacheService;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.pollution.progress.PollutionImportProgress;
import pl.pollub.is.backend.progress.ProgressService;
import pl.pollub.is.backend.progress.model.ProgressStatus;
import pl.pollub.is.backend.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Slf4j(topic = "Air Pollution Service")
public class AirPollutionService {
    private final static List<String> ALLOWED_INDICATORS = List.of("SO2", "NO2", "PM2,5", "Pb(PM10)", "NOx");
    private final static List<String> COLUMN_NAMES = List.of("Rok", "Województwo", "Kod strefy", "Kod stacji", "Wskaźnik", "Czas uśredniania", "Średnia", "Liczba pomiarów");

    private final AirPollutionRepository airPollutionRepository;
    private final DatabaseCacheService cacheService;
    private final PollutionImportProgress progress = new PollutionImportProgress();
    private final EntityManagerFactory entityManagerFactory;
    private final ThreadPoolTaskExecutor asyncExecutor;

    public AirPollutionService(AirPollutionRepository airPollutionRepository,
                               DatabaseCacheService cacheService,
                               ProgressService progressService,
                               EntityManagerFactory entityManagerFactory,
                               ThreadPoolTaskExecutor asyncExecutor) {
        this.airPollutionRepository = airPollutionRepository;
        this.cacheService = cacheService;
        this.entityManagerFactory = entityManagerFactory;
        this.asyncExecutor = asyncExecutor;
        progressService.registerProgress(progress);
    }

    public ResponseEntity<String> handleFileUpload(MultipartFile multipartFile) {
        if (progress.getStatus() == ProgressStatus.IN_PROGRESS)
            throw new HttpException(HttpStatus.CONFLICT, "Operation already in progress");

        File file = FileUtil.multipartToFile(multipartFile);

        progress.clear();
        log.info("Started processing air pollution data from file: {}", multipartFile.getOriginalFilename());

        // start processing file in background
        asyncExecutor.execute(() -> {
            progress.setStatus(ProgressStatus.IN_PROGRESS);
            progress.setStartDate();
            try (ReadableWorkbook wb = new ReadableWorkbook(file)) {
                progress.setDataLoaded(true);
                airPollutionRepository.deleteAllInBatch();

                // first count rows in all sheets
                wb.getSheets().forEach(this::preprocessSheet);
                wb.getSheets().forEach(this::processSheet);

                progress.setEndDate();
                progress.setStatus(ProgressStatus.FINISHED);
                log.info("Finished processing air pollution data from file: {}", multipartFile.getOriginalFilename());
            } catch (IOException e) {
                progress.setStatus(ProgressStatus.FAILED);
                progress.setEndDate();
                log.error("Error while reading excel file", e);
                throw new HttpException(500, "Error while reading excel file");
            }
        });

        return progress.toResponseEntity();
    }

    private void preprocessSheet(Sheet sheet) {
        if (!ALLOWED_INDICATORS.contains(sheet.getName()))
            return;

        try (Stream<Row> rowStream = sheet.openStream()) {
            long count = rowStream.count() - 2;
            progress.setIndicatorTotal(sheet.getName(), count);
            progress.setTotal(progress.getTotal() + count);
        } catch (IOException e) {
            throw new HttpException(500, "Error while reading excel file");
        }
    }

    private void processSheet(Sheet sheet) {
        if (!ALLOWED_INDICATORS.contains(sheet.getName()))
            return;

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
                // skip first two rows
                if (row.getRowNum() == 2) return;

                progress.addRead(1);
                progress.addIndicatorRead(sheet.getName(), 1);
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
            });

            progress.setIndicatorReadDate(sheet.getName());

            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            int saved = 0;
            for (AirPollution airPollution : toSave) {
                entityManager.persist(airPollution);
                progress.addSaved(1);
                progress.addIndicatorSaved(sheet.getName(), 1);
                saved++;
            }
            log.info("Saved {} air pollution records for indicator {}", saved, sheet.getName());
            entityManager.getTransaction().commit();
            entityManager.clear();
            entityManager.close();

            progress.setIndicatorSavedDate(sheet.getName());

            if (progress.getRead() == progress.getTotal())
                progress.setReadDate();

            cacheService.onDependencyChange(CacheDependency.POLLUTION_DATA);
        } catch (IOException e) {
            throw new HttpException(500, "Error while reading excel file");
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
