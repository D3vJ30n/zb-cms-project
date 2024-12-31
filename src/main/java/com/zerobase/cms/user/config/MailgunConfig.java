package com.zerobase.cms.user.config;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailgunConfig {

    @Value("${mailgun.api-key}")
    private String apiKey;

    private static final String MAILGUN_URL = "https://api.mailgun.net/v3/";
    private static final String DOMAIN = "sandbox0dc258dd80fd43fe96e4a6c52b47e8c8.mailgun.org"; // 실제 도메인으로 변경 필요

    public boolean sendEmail(String to, String subject, String text) {
        try {
            HttpResponse<JsonNode> response = Unirest.post(MAILGUN_URL + DOMAIN + "/messages")
                .basicAuth("api", apiKey)
                .queryString("from", "no-reply@" + DOMAIN)
                .queryString("to", to)
                .queryString("subject", subject)
                .queryString("text", text)
                .asJson();

            return response.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
