package com.air.airquality.services;
import com.air.airquality.model.AirQualityData;
import com.air.airquality.repository.AirQualityRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class AirQualityService {
    @Autowired
    private AirQualityRepository airQualityRepository;
    @Autowired
    private AlertService alertService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://api.openaq.org/v2/latest?city=Delhi&parameter=pm25";

    @Scheduled(fixedRate = 3600000) // every 1 hour
    public void fetchAQIData() {
        try {
            String response = restTemplate.getForObject(API_URL, String.class);
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject location = results.getJSONObject(i);
                JSONArray measurements = location.getJSONArray("measurements");

                if (!measurements.isEmpty()) {
                    JSONObject aqi = measurements.getJSONObject(0);

                    AirQualityData data = new AirQualityData();
                    data.setCity(location.getString("city"));
                    data.setValue(aqi.getDouble("value"));
                    data.setUnit(aqi.getString("unit"));
                    data.setTimestamp(LocalDateTime.now());

                    // ✅ Save AQI data to DB
                    airQualityRepository.save(data);

                    // ✅ Check if AQI exceeds threshold and trigger SMS
                    alertService.checkAndSendAlert(data);
                }
            }
            System.out.println("✅ AQI data fetched and processed.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to fetch AQI data.");
        }
    }
    public List<AirQualityData> getLatest(String city) {
        return airQualityRepository.findTop10ByCityOrderByTimestampDesc(city);
    }
    public void saveData(AirQualityData data) {
        airQualityRepository.save(data);
    }
}