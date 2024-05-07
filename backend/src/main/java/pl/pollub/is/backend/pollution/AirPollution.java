package pl.pollub.is.backend.pollution;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "air_pollution")
@Data
public class AirPollution {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "station_code")
    private String stationCode;

    @Column(name = "voivodeship")
    private String voivodeship;

    @Column(name = "year")
    private int year;

    @Column(name = "indicator")
    private String indicator;

    @Column(name = "average")
    private Double average;

    @Column(name = "averaging_period")
    private String averagingPeriod;

    @Column(name = "samples")
    private Integer samples;
}
