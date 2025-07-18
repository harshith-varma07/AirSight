package com.air.airquality.controller;

import com.air.airquality.model.AirQualityData;
import com.air.airquality.services.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/air")
public class AirQualityController {
    @Autowired
    private AirQualityService service;

    // Fetch and store latest data for a city (triggered by user)
    @PostMapping("/fetch/{city}")
    public String fetchForCity(@PathVariable String city) {
        service.fetchAQIDataForCity(city);
        return "Data fetched for " + city;
    }

    // Get all latest data for a city
    @GetMapping("/{city}")
    public List<AirQualityData> getLatestForCity(@PathVariable String city) {
        return service.getLatestForCity(city);
    }

    // Get latest data for a city and parameter
    @GetMapping("/{city}/{parameter}")
    public List<AirQualityData> getLatestForCityAndParameter(@PathVariable String city, @PathVariable String parameter) {
        return service.getLatestForCityAndParameter(city, parameter);
    }
}
