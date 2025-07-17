package com.air.airquality.controller;
import com.air.airquality.model.AirQualityData;
import com.air.airquality.services.AirQualityService;
import com.air.airquality.repository.AirQualityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/air")
public class AirQualityController {

    @Autowired
    private AirQualityService service;

    @Autowired
    private AirQualityRepository airRepo;
    @PostMapping("/add")
    public void addData(@RequestBody AirQualityData data) {
        service.saveData(data);
    }
    @GetMapping("/history/{city}")
    @PreAuthorize("hasRole('USER')") // Optional if you secured via config
    public ResponseEntity<List<AirQualityData>> getHistoricalAQI(@PathVariable String city) {
        List<AirQualityData> list = airRepo.findTop10ByCityOrderByTimestampDesc(city);
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{city}")
    public List<AirQualityData> getLatestAQI(@PathVariable String city) {
        return service.getLatest(city);
    }

}
