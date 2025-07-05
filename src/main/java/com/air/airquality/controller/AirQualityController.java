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

    @GetMapping("/{location}/latest")
    public List<AirQualityData> getByLocation(@PathVariable String location) {
        return service.getLatest(location);
    }

    @PostMapping("/add")
    public void addData(@RequestBody AirQualityData data) {
        service.saveData(data);
    }
}
