package com.air.airquality.services;

import com.air.airquality.model.User;
import com.air.airquality.model.UserAlert;
import com.air.airquality.repository.UserAlertRepository;
import com.air.airquality.repository.UserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class AlertService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAlertRepository userAlertRepository;
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;
    
    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }
    
    public void checkAndSendAlerts(String city, Integer aqiValue) {
        List<User> usersToAlert = userRepository.findUsersForAlert(city, aqiValue);
        
        for (User user : usersToAlert) {
            try {
                sendSmsAlert(user, city, aqiValue);
                
                // Log the alert
                UserAlert alert = new UserAlert(user, city, aqiValue, user.getAlertThreshold());
                alert.setAlertSent(true);
                userAlertRepository.save(alert);
                
            } catch (Exception e) {
                System.err.println("Failed to send alert to user " + user.getUsername() + ": " + e.getMessage());
                
                // Log failed alert
                UserAlert alert = new UserAlert(user, city, aqiValue, user.getAlertThreshold());
                alert.setAlertSent(false);
                userAlertRepository.save(alert);
            }
        }
    }
    
    private void sendSmsAlert(User user, String city, Integer aqiValue) {
        String messageBody = String.format(
            "AIR QUALITY ALERT!\n" +
            "City: %s\n" +
            "Current AQI: %d\n" +
            "Your threshold: %d\n" +
            "Category: %s\n" +
            "Please take necessary precautions!",
            city, aqiValue, user.getAlertThreshold(), getAqiCategory(aqiValue)
        );
        
        Message message = Message.creator(
            new PhoneNumber(user.getPhoneNumber()),
            new PhoneNumber(twilioPhoneNumber),
            messageBody
        ).create();
        
        System.out.println("SMS sent to " + user.getPhoneNumber() + " with SID: " + message.getSid());
    }
    
    private String getAqiCategory(Integer aqi) {
        if (aqi <= 50) return "Good";
        else if (aqi <= 100) return "Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Unhealthy";
        else if (aqi <= 300) return "Very Unhealthy";
        else return "Hazardous";
    }
}