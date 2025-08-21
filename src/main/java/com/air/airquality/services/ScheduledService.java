package com.air.airquality.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
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
    
    // Optimized scheduled task using async processing
    @Scheduled(fixedRate = 300000, initialDelay = 60000) // Every 5 minutes, 1 minute initial delay
    public void updateAqiData() {
        logger.info("Starting scheduled AQI data update");
        
        try {
            // Run data update and alert processing asynchronously
            CompletableFuture<Void> updateTask = CompletableFuture.runAsync(() -> {
                openAQService.updateAllCitiesData();
            });
            
            CompletableFuture<Void> alertTask = CompletableFuture.runAsync(() -> {
                alertService.processAlerts();
            });
            
            // Wait for both tasks to complete
            CompletableFuture.allOf(updateTask, alertTask)
                    .thenRun(() -> logger.info("Scheduled update completed successfully"))
                    .exceptionally(ex -> {
                        logger.error("Error during scheduled update: {}", ex.getMessage());
                        return null;
                    });
                    
        } catch (Exception e) {
            logger.error("Error in scheduled service: {}", e.getMessage(), e);
        }
    }

    // Cleanup old data weekly
    @Scheduled(cron = "0 0 2 * * SUN") // Every Sunday at 2 AM
    public void cleanupOldData() {
        logger.info("Starting weekly data cleanup");
        try {
            aqiService.cleanupOldData(90); // Keep 90 days of data
            logger.info("Weekly cleanup completed");
        } catch (Exception e) {
            logger.error("Error during cleanup: {}", e.getMessage());
        }
    }
}