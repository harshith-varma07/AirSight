package com.air.airquality.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

@Service
public class SmsService {

    @Value("${fast2sms.api.key}")
    private String apiKey;

    public void sendSms(String phoneNumber, String messageText) {
        try {
            String apiUrl = "https://www.fast2sms.com/dev/bulkV2";
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(apiUrl);

            JSONObject body = new JSONObject();
            body.put("route", "q"); // quick transactional
            body.put("sender_id", "FSTSMS");
            body.put("message", messageText);
            body.put("language", "english");
            body.put("flash", 0);
            body.put("numbers", phoneNumber); // comma-separated if multiple

            post.setHeader("authorization", apiKey);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(body.toString()));

            HttpResponse response = client.execute(post);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("📩 SMS Sent! Response: " + result);

            client.close();
        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }
}