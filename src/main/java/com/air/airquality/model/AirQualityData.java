package com.air.airquality.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AirQualityData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String parameter; // pm25, pm10, so2, o3, co, bc, no2
    private double value;
    private String unit;
    private LocalDateTime timestamp;

    public AirQualityData() {
    }

    public AirQualityData(String city, String parameter, double value, String unit, LocalDateTime timestamp) {
        this.city = city;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}