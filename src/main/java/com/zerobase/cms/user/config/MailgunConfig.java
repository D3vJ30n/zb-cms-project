package com.zerobase.cms.user.config;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MailgunConfig {
    @Value("${mailgun.api-key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from}")
    private String from;

    // 이메일 보내기 메서드
    public boolean sendEmail(String to, String subject, String text) {
        try {
            HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + domain + "/messages")
                .basicAuth("api", apiKey)
                .queryString("from", from)
                .queryString("to", to)
                .queryString("subject", subject)
                .queryString("text", text)
                .asJson();
            
            return request.getStatus() == 200;
        } catch (UnirestException e) {
            e.printStackTrace();
            return false;
        }
    }
}
