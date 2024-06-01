package pl.pollub.is.backend.pollution.calculations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/air-pollution")
@RequiredArgsConstructor
public class AirPollutionCountController {
    private final AirPollutionCountService airPollutionService;

    @GetMapping("/counts/average-by-indicator-and-year")
    public ResponseEntity<List<Object[]>> getAveragePollutionByIndicatorAndYear(
            @RequestParam String indicator,
            @RequestParam int year) {
        List<Object[]> averagePollutionData = airPollutionService.getAveragePollutionByIndicatorAndYear(indicator, year);
        return ResponseEntity.ok(averagePollutionData);
    }

    @GetMapping("/counts/average-by-indicator-and-year-voivodeship")
    public ResponseEntity<List<Object[]>> getAveragePollutionByIndicatorAndYearInVoivodeship(
            @RequestParam String indicator,
            @RequestParam int year,
            @RequestParam(required = false) String voivodeship) {
        List<Object[]> averagePollutionData = airPollutionService.getAveragePollutionByIndicatorAndYearInVoivodeship(indicator, year, voivodeship);
        return ResponseEntity.ok(averagePollutionData);
    }

    @GetMapping("/counts/average-by-year")
    public ResponseEntity<List<Object[]>> getAveragePollutionByYear(
            @RequestParam int year ){
        List<Object[]> averagePollutionData = airPollutionService.getAveragePollutionByYear(year);
        return ResponseEntity.ok(averagePollutionData);
    }
}
