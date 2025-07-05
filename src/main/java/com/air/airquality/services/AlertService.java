package com.air.airquality.services;
import com.air.airquality.model.AirQualityData;
import com.air.airquality.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
@Service
public class AlertService {
    @Autowired
    private SmsService smsService;
    @Autowired
    private UserRepository userrepository; // Assuming User is a singleton or managed bean
    public void checkAndAlert(AirQualityData data) {
        try {
            if (data.getValue() > 150) {
                String message = "⚠️ High AQI Alert in " + data.getLocation() + ": AQI = " + data.getValue();
                String num= userrepository.findByUsername("admin")
                        .orElseThrow(() -> new RuntimeException("Admin user not found"))
                        .getPhoneNumber(); // Assuming User has a getPhoneNumber method
                smsService.sendSms(num, message); // Replace with real number
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}