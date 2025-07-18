package com.air.airquality.services;

import com.air.airquality.model.AirQualityData;
import com.air.airquality.repository.AirQualityRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirQualityService {
    @Autowired
    private AirQualityRepository airQualityRepository;

    private final String[] PARAMETERS = {"pm25", "pm10", "so2", "o3", "co", "bc", "no2"};

    public void fetchAQIDataForCity(String city) {
        RestTemplate restTemplate = new RestTemplate();
        for (String param : PARAMETERS) {
            String url = "https://api.openaq.org/v2/latest?city=" + city + "&parameter=" + param;
            try {
                String response = restTemplate.getForObject(url, String.class);
                JSONObject json = new JSONObject(response);
                JSONArray results = json.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject location = results.getJSONObject(i);
                    JSONArray measurements = location.getJSONArray("measurements");
                    for (int j = 0; j < measurements.length(); j++) {
                        JSONObject aqi = measurements.getJSONObject(j);
                        AirQualityData data = new AirQualityData();
                        data.setCity(location.getString("city"));
                        data.setParameter(aqi.getString("parameter"));
                        data.setValue(aqi.getDouble("value"));
                        data.setUnit(aqi.getString("unit"));
                        data.setTimestamp(LocalDateTime.now());
                        airQualityRepository.save(data);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<AirQualityData> getLatestForCity(String city) {
        return airQualityRepository.findByCity(city);
    }

    public List<AirQualityData> getLatestForCityAndParameter(String city, String parameter) {
        return airQualityRepository.findTop10ByCityAndParameterOrderByTimestampDesc(city, parameter);
    }
}