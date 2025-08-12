package com.air.airquality.controller;

import com.air.airquality.dto.AqiResponse;
import com.air.airquality.model.AqiData;
import com.air.airquality.services.AqiService;
import com.air.airquality.services.OpenAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aqi")
@CrossOrigin(origins = "*")
public class AqiController {
    
    private static final Logger logger = LoggerFactory.getLogger(AqiController.class);
    
    @Autowired
    private AqiService aqiService;
    
    @Autowired
    private OpenAQService openAQService;
    
    // Public endpoint - accessible by all users
    @GetMapping("/current/{city}")
    public ResponseEntity<?> getCurrentAqi(@PathVariable String city) {
        try {
            logger.info("Fetching current AQI for city: {}", city);
            AqiData aqiData = openAQService.getCurrentAqiData(city);
            
            AqiResponse response = new AqiResponse(
                aqiData.getCity(),
                aqiData.getAqiValue(),
                aqiData.getPm25(),
                aqiData.getPm10(),
                aqiData.getNo2(),
                aqiData.getSo2(),
                aqiData.getCo(),
                aqiData.getO3(),
                aqiData.getTimestamp()
            );
            
            // Add additional metadata
            response.setCategory(openAQService.getAqiCategory(aqiData.getAqiValue()));
            response.setDescription(openAQService.getAqiDescription(aqiData.getAqiValue()));
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("message", "AQI data retrieved successfully");
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error fetching current AQI for city {}: {}", city, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unable to fetch AQI data for " + city + ". Please try again later.");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // Protected endpoint - only for registered users
    @GetMapping("/historical/{city}")
    public ResponseEntity<?> getHistoricalData(
            @PathVariable String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest request) {
        
        try {
            // Check if user is authenticated (simple check for demo)
            String userId = request.getHeader("X-User-Id");
            if (userId == null || userId.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Historical data access requires user registration and login");
                response.put("requiresAuth", true);
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            logger.info("Fetching historical data for city: {} from {} to {} for user: {}", 
                       city, startDate, endDate, userId);
            
            List<AqiResponse> historicalData = aqiService.getHistoricalData(city, startDate, endDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", historicalData);
            response.put("count", historicalData.size());
            response.put("city", city);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("message", "Historical data retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching historical data for city {}: {}", city, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unable to fetch historical data for " + city);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Public endpoint - list of available cities
    @GetMapping("/cities")
    public ResponseEntity<?> getAvailableCities() {
        try {
            logger.info("Fetching list of available cities");
            List<String> cities = openAQService.getAvailableCities();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cities", cities);
            response.put("count", cities.size());
            response.put("message", "Available cities retrieved successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching available cities: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unable to fetch available cities");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Public endpoint - search cities (fuzzy search)
    @GetMapping("/search")
    public ResponseEntity<?> searchCities(@RequestParam String query) {
        try {
            logger.info("Searching cities with query: {}", query);
            
            List<String> matchingCities = openAQService.searchCities(query);
            
            // If exact matches found, get current AQI for the first match
            AqiData currentData = null;
            if (!matchingCities.isEmpty()) {
                try {
                    currentData = openAQService.getCurrentAqiData(matchingCities.get(0));
                } catch (Exception e) {
                    logger.warn("Could not get current data for city: {}", matchingCities.get(0));
                }
            }
            
            // If no matches found and query looks like a city name, try to add it
            if (matchingCities.isEmpty() && query.trim().length() > 2) {
                logger.info("No matches found for '{}', attempting to add as new city", query);
                boolean added = openAQService.addCityToMonitoring(query.trim());
                if (added) {
                    matchingCities = List.of(query.trim());
                    try {
                        currentData = openAQService.getCurrentAqiData(query.trim());
                    } catch (Exception e) {
                        logger.warn("Could not get current data for newly added city: {}", query);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cities", matchingCities);
            response.put("query", query);
            response.put("found", matchingCities.size());
            
            if (currentData != null) {
                AqiResponse aqiResponse = new AqiResponse(
                    currentData.getCity(),
                    currentData.getAqiValue(),
                    currentData.getPm25(),
                    currentData.getPm10(),
                    currentData.getNo2(),
                    currentData.getSo2(),
                    currentData.getCo(),
                    currentData.getO3(),
                    currentData.getTimestamp()
                );
                aqiResponse.setCategory(openAQService.getAqiCategory(currentData.getAqiValue()));
                aqiResponse.setDescription(openAQService.getAqiDescription(currentData.getAqiValue()));
                response.put("currentData", aqiResponse);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error searching cities with query {}: {}", query, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Search failed");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Public endpoint - get multiple cities data at once
    @GetMapping("/multiple")
    public ResponseEntity<?> getMultipleCitiesAqi(@RequestParam List<String> cities) {
        try {
            logger.info("Fetching AQI for multiple cities: {}", cities);
            
            Map<String, AqiResponse> citiesData = new HashMap<>();
            
            for (String city : cities) {
                try {
                    AqiData aqiData = openAQService.getCurrentAqiData(city);
                    AqiResponse response = new AqiResponse(
                        aqiData.getCity(),
                        aqiData.getAqiValue(),
                        aqiData.getPm25(),
                        aqiData.getPm10(),
                        aqiData.getNo2(),
                        aqiData.getSo2(),
                        aqiData.getCo(),
                        aqiData.getO3(),
                        aqiData.getTimestamp()
                    );
                    response.setCategory(openAQService.getAqiCategory(aqiData.getAqiValue()));
                    response.setDescription(openAQService.getAqiDescription(aqiData.getAqiValue()));
                    citiesData.put(city, response);
                } catch (Exception e) {
                    logger.warn("Failed to get data for city: {}", city);
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", citiesData);
            result.put("requestedCities", cities);
            result.put("foundCities", citiesData.keySet());
            result.put("count", citiesData.size());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error fetching multiple cities AQI: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Unable to fetch data for requested cities");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Public endpoint - add new city to monitoring
    @PostMapping("/cities/add")
    public ResponseEntity<?> addCityToMonitoring(@RequestParam String city) {
        try {
            logger.info("Request to add new city to monitoring: {}", city);
            
            boolean success = openAQService.addCityToMonitoring(city);
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                // Get the newly added city data
                AqiData aqiData = openAQService.getCurrentAqiData(city);
                AqiResponse aqiResponse = new AqiResponse(
                    aqiData.getCity(),
                    aqiData.getAqiValue(),
                    aqiData.getPm25(),
                    aqiData.getPm10(),
                    aqiData.getNo2(),
                    aqiData.getSo2(),
                    aqiData.getCo(),
                    aqiData.getO3(),
                    aqiData.getTimestamp()
                );
                aqiResponse.setCategory(openAQService.getAqiCategory(aqiData.getAqiValue()));
                aqiResponse.setDescription(openAQService.getAqiDescription(aqiData.getAqiValue()));
                
                response.put("success", true);
                response.put("message", "City successfully added to monitoring");
                response.put("city", city);
                response.put("data", aqiResponse);
            } else {
                response.put("success", false);
                response.put("message", "Failed to add city to monitoring. City may not exist or data unavailable.");
                response.put("city", city);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error adding city to monitoring {}: {}", city, e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error adding city to monitoring");
            response.put("city", city);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
