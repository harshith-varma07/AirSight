package com.air.airquality.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class ScheduledService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledService.class);
    
    @Autowired
    private OpenAQService openAQService;
    
    @Autowired
    private AlertService alertService;
    
    @Autowired
    private AqiService aqiService;
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void fetchAqiDataScheduled() {
        logger.info("Starting scheduled AQI data fetch...");
        
        try {
            // Get all cities from database
            List<String> citiesToMonitor = openAQService.getAvailableCities();
            
            logger.info("Fetching AQI data for {} cities", citiesToMonitor.size());
            
            for (String city : citiesToMonitor) {
                try {
                    logger.debug("Fetching data for city: {}", city);
                    openAQService.fetchAndStoreAqiData(city);
                    
                    // Check for alerts after fetching new data
                    try {
                        var currentData = openAQService.getCurrentAqiData(city);
                        alertService.checkAndSendAlerts(city, currentData.getAqiValue());
                    } catch (Exception e) {
                        logger.warn("Error checking alerts for city {}: {}", city, e.getMessage());
                    }
                    
                    // Add small delay between API calls to be respectful to the API
                    Thread.sleep(1000);
                    
                } catch (InterruptedException e) {
                    logger.warn("Scheduled task interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("Error in scheduled task for city {}: {}", city, e.getMessage());
                }
            }
            
            logger.info("Completed scheduled AQI data fetch for {} cities", citiesToMonitor.size());
            
        } catch (Exception e) {
            logger.error("Error in scheduled AQI data fetch: {}", e.getMessage(), e);
        }
    }
    
    // Run once daily at 2 AM to clean up old data (optional)
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldData() {
        try {
            logger.info("Starting cleanup of old AQI data...");
            
            // Here you could add logic to remove data older than a certain period
            // For example, keep only last 90 days of data
            // aqiDataRepository.deleteDataOlderThan(LocalDateTime.now().minusDays(90));
            
            logger.info("Cleanup of old AQI data completed");
            
        } catch (Exception e) {
            logger.error("Error during data cleanup: {}", e.getMessage(), e);
        }
    }
}