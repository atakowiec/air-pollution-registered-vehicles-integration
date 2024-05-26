package pl.pollub.is.backend.vehicles.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name= "vehicles")
@Data
public class Vehicle {
    @Id
    @Column(name="id", insertable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="vehicle_id", unique = true, nullable = false)
    private BigInteger vehicleId;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "county_code")
    private String countyCode;

    @Column(name= "brand")
    private String brand;

    @Column(name= "model")
    private String model;

    @Column(name= "type")
    private String type;

    @Column(name="sub_type")
    private String subType;

    @Column(name="manufacture_year")
    private Integer manufactureYear;

    @Column(name="manufacture_method")
    private String manufactureMethod;

    @Column(name="first_registration_date")
    private Date firstRegistrationDate;

    @Column(name="engine_capacity")
    private Double engineCapacity;

    @Column(name="engine_power")
    private Double enginePower;

    @Column(name="hybrid_engine_power")
    private Double hybridEnginePower;

    @Column(name="curb_weight")
    private Double curbWeight;

    @Column(name="fuel_type")
    private String fuelType;

    @Column(name="alternative_fuel_type")
    private String alternativeFuelType;

    @Column(name="alternative_fuel_type2")
    private String alternativeFuelType2;

    @Column(name="average_fuel_consumption", precision = 2)
    private Double averageFuelConsumption;

    @Column(name="deregistration_date")
    private Date deregistrationDate;

    @Column (name="vehicles_owner_area")
    private String vehiclesOwnerArea;

    @Column(name="fuel_co2_emission")
    private Double fuelCo2Emission;

    @Column(name="alternative_fuel_co2_emission")
    private Double alternativeFuelCo2Emission;
}
