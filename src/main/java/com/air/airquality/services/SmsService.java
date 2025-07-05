package com.air.airquality.services;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
@Service
public class SmsService {

    @Value("${fast2sms.api.key}")
    private String apiKey;

    public void sendSms(String phoneNumber, String message) throws IOException {
        String url = "https://www.fast2sms.com/dev/bulkV2";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // Set headers
        post.setHeader("authorization", apiKey);
        post.setHeader("Content-Type", "application/json");

        // Set body
        JSONObject body = new JSONObject();
        body.put("route", "q"); // For quick transactional SMS
        body.put("sender_id", "FSTSMS");
        body.put("message", message);
        body.put("language", "english");
        body.put("flash", 0);
        body.put("numbers", phoneNumber); // comma-separated numbers if needed

        post.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));

        HttpResponse response = client.execute(post);
        String result = EntityUtils.toString(response.getEntity());
        System.out.println("SMS Response: " + result);
    }
}