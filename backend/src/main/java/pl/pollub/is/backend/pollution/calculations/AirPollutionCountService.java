package pl.pollub.is.backend.pollution.calculations;

import org.springframework.stereotype.Service;
import pl.pollub.is.backend.pollution.AirPollutionRepository;

import java.util.List;

@Service
public class AirPollutionCountService {
    private final AirPollutionRepository airPollutionRepository;

    public AirPollutionCountService(AirPollutionRepository airPollutionRepository) {
        this.airPollutionRepository = airPollutionRepository;
    }

    public List<Object[]> getAveragePollutionByIndicatorAndYear(String indicator, int year) {
        System.out.println("Fetching average pollution for indicator: " + indicator + " and year: " + year);
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYear(indicator, year);
        System.out.println("Found " + result.size() + " voivodeships with average pollution data");
        return result;
    }

    public List<Object[]> getAveragePollutionByIndicatorAndYearInVoivodeship(String indicator, int year, String voivodeship) {
        System.out.println("Fetching average pollution for indicator: " + indicator + ", year: " + year + ", voivodeship: " + voivodeship);
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYearInVoivodeship(indicator, year, voivodeship);
        System.out.println("Found " + result.size() + " records with average pollution data");
        return result;
    }
}
