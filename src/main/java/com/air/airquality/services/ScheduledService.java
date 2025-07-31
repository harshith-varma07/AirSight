package com.air.airquality.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
@Service
public class ScheduledService {
    
    @Autowired
    private OpenAQService openAQService;
    
    @Autowired
    private AlertService alertService;
    
    @Autowired
    private AqiService aqiService;
    
    // List of major cities to monitor
    private final List<String> citiesToMonitor = Arrays.asList(
        "Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata", 
        "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Surat",
        "New York", "London", "Paris", "Tokyo", "Beijing"
    );
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void fetchAqiDataScheduled() {
        System.out.println("Starting scheduled AQI data fetch...");
        
        for (String city : citiesToMonitor) {
            try {
                openAQService.fetchAndStoreAqiData(city);
                
                // Check for alerts after fetching new data
                try {
                    var currentAqi = aqiService.getCurrentAqi(city);
                    alertService.checkAndSendAlerts(city, currentAqi.getAqiValue());
                } catch (Exception e) {
                    System.err.println("Error checking alerts for city " + city + ": " + e.getMessage());
                }
                
                // Add small delay between API calls
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("Error in scheduled task for city " + city + ": " + e.getMessage());
            }
        }
        
        System.out.println("Completed scheduled AQI data fetch.");
    }
}