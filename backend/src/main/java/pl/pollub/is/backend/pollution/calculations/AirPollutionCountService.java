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
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYear(indicator, year);
        return result;
    }

    public List<Object[]> getAveragePollutionByIndicatorAndYearInVoivodeship(String indicator, int year, String voivodeship) {
        List<Object[]> result = airPollutionRepository.findAveragePollutionByIndicatorAndYearInVoivodeship(indicator, year, voivodeship);
        return result;
    }
}
