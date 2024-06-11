package pl.pollub.is.backend.pollution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AirPollutionRepository extends JpaRepository<AirPollution, Integer> {

    @Query(value ="SELECT voivodeship, AVG(average) as avg_pollution " +
            "FROM air_pollution " +
            "WHERE indicator = :indicator AND year = :year " +
            "GROUP BY voivodeship " +
            "ORDER BY avg_pollution DESC",
            nativeQuery = true)
    List<Object[]> findAveragePollutionByIndicatorAndYear(
            @Param("indicator") String indicator,
            @Param("year") int year
    );

    @Query(value = "SELECT voivodeship, AVG(average) as avg_pollution " +
            "FROM air_pollution " +
            "WHERE indicator = :indicator AND year = :year " +
            "AND voivodeship = :voivodeship " +
            "GROUP BY voivodeship",
            nativeQuery = true)
    List<Object[]> findAveragePollutionByIndicatorAndYearInVoivodeship(
            @Param("indicator") String indicator,
            @Param("year") int year,
            @Param("voivodeship") String voivodeship
    );

    @Query(value = "SELECT voivodeship, indicator, AVG(average) as avg_pollution " +
            "FROM air_pollution " +
            "WHERE year = :year " +
            "GROUP BY voivodeship, indicator",
            nativeQuery = true)
    List<Object[]> findAveragePollutionByYear(@Param("year") int year);

    @Query(value = "SELECT year, voivodeship, indicator, AVG(average) FROM `air_pollution` " +
            "WHERE year BETWEEN 2000 AND 2019 " +
            "GROUP BY `year`, `voivodeship`, `indicator`", nativeQuery = true)
    List<Object[]> averageByYearVoivodeshipAndIndicator();

}
