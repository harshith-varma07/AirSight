package com.air.airquality.services;
import com.air.airquality.model.AirQualityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class AlertService {
    @Autowired
    private SmsService smsService;
    private static final double AQI_THRESHOLD = 150;
    public void checkAndSendAlert(AirQualityData data) {
        if (data.getValue() > AQI_THRESHOLD) {
            String message = "⚠️ High AQI Alert!\nCity: " + data.getCity() +
                    "\nPM2.5: " + data.getValue() + " " + data.getUnit();
            smsService.sendSms("91XXXXXXXXXX", message); // Replace with actual number
        }
    }
}