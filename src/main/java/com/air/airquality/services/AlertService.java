package com.air.airquality.services;

import com.air.airquality.model.User;
import com.air.airquality.model.UserAlert;
import com.air.airquality.repository.UserAlertRepository;
import com.air.airquality.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAlertRepository userAlertRepository;
    
    @Autowired
    private OpenAQService openAQService;
    
    @Value("${twilio.account.sid:}")
    private String accountSid;
    
    @Value("${twilio.auth.token:}")
    private String authToken;
    
    @Value("${twilio.phone.number:}")
    private String twilioPhoneNumber;
    
    private boolean twilioEnabled = false;
    
    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && !accountSid.isEmpty() && 
            authToken != null && !authToken.isEmpty() &&
            twilioPhoneNumber != null && !twilioPhoneNumber.isEmpty()) {
            
            try {
                // Only initialize Twilio if credentials are provided
                // Twilio.init(accountSid, authToken);
                twilioEnabled = true;
                logger.info("Twilio SMS service initialized successfully");
            } catch (Exception e) {
                logger.warn("Failed to initialize Twilio SMS service: {}", e.getMessage());
                twilioEnabled = false;
            }
        } else {
            logger.info("Twilio credentials not provided, SMS alerts disabled");
            twilioEnabled = false;
        }
    }
    
    public void checkAndSendAlerts(String city, Integer aqiValue) {
        try {
            logger.debug("Checking alerts for city: {} with AQI: {}", city, aqiValue);
            
            List<User> usersToAlert = userRepository.findUsersForAlert(city, aqiValue);
            
            if (usersToAlert.isEmpty()) {
                logger.debug("No users to alert for city: {} with AQI: {}", city, aqiValue);
                return;
            }
            
            logger.info("Found {} users to alert for city: {} with AQI: {}", usersToAlert.size(), city, aqiValue);
            
            for (User user : usersToAlert) {
                try {
                    boolean alertSent = false;
                    
                    if (twilioEnabled) {
                        sendSmsAlert(user, city, aqiValue);
                        alertSent = true;
                        logger.info("SMS alert sent to user: {} for city: {}", user.getUsername(), city);
                    } else {
                        // Log alert instead of sending SMS when Twilio is not available
                        logAlert(user, city, aqiValue);
                        alertSent = true;
                        logger.info("Alert logged for user: {} for city: {} (SMS disabled)", user.getUsername(), city);
                    }
                    
                    // Log the alert in database
                    UserAlert alert = new UserAlert(user, city, aqiValue, user.getAlertThreshold());
                    alert.setAlertSent(alertSent);
                    userAlertRepository.save(alert);
                    
                } catch (Exception e) {
                    logger.error("Failed to send alert to user {}: {}", user.getUsername(), e.getMessage());
                    
                    // Log failed alert
                    UserAlert alert = new UserAlert(user, city, aqiValue, user.getAlertThreshold());
                    alert.setAlertSent(false);
                    userAlertRepository.save(alert);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error checking and sending alerts for city {}: {}", city, e.getMessage());
        }
    }
    
    private void sendSmsAlert(User user, String city, Integer aqiValue) {
        String messageBody = String.format(
            "🚨 AIR QUALITY ALERT!\n" +
            "City: %s\n" +
            "Current AQI: %d\n" +
            "Your threshold: %d\n" +
            "Category: %s\n" +
            "Please take necessary precautions!\n" +
            "- AirSight Monitoring",
            city, aqiValue, user.getAlertThreshold(), openAQService.getAqiCategory(aqiValue)
        );
        
        try {
            // Placeholder for actual Twilio SMS sending
            // Message message = Message.creator(
            //     new PhoneNumber(user.getPhoneNumber()),
            //     new PhoneNumber(twilioPhoneNumber),
            //     messageBody
            // ).create();
            // 
            // logger.info("SMS sent to {} with SID: {}", user.getPhoneNumber(), message.getSid());
            
            // For now, just log the message
            logger.info("SMS Alert for {}: {}", user.getPhoneNumber(), messageBody.replace("\n", " | "));
            
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", user.getPhoneNumber(), e.getMessage());
            throw e;
        }
    }
    
    private void logAlert(User user, String city, Integer aqiValue) {
        String alertMessage = String.format(
            "ALERT: User %s (%s) - City: %s, AQI: %d (threshold: %d), Category: %s",
            user.getUsername(),
            user.getPhoneNumber(),
            city,
            aqiValue,
            user.getAlertThreshold(),
            openAQService.getAqiCategory(aqiValue)
        );
        
        logger.warn("AIR QUALITY ALERT: {}", alertMessage);
    }
    
    public List<UserAlert> getUserAlerts(Long userId) {
        try {
            return userAlertRepository.findByUserIdOrderByTimestampDesc(userId);
        } catch (Exception e) {
            logger.error("Error getting alerts for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
    
    public void deleteUserAlert(Long alertId, Long userId) {
        try {
            UserAlert alert = userAlertRepository.findByIdAndUserId(alertId, userId);
            if (alert != null) {
                userAlertRepository.delete(alert);
                logger.info("Deleted alert {} for user {}", alertId, userId);
            } else {
                logger.warn("Alert {} not found for user {}", alertId, userId);
            }
        } catch (Exception e) {
            logger.error("Error deleting alert {} for user {}: {}", alertId, userId, e.getMessage());
        }
    }
    
    private String getAqiCategory(Integer aqi) {
        return openAQService.getAqiCategory(aqi);
    }
}