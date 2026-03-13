package com.rwaknow.smartstore.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MpesaConfig {

    @Value("${mpesa.consumer-key}")
    private String consumerKey;

    @Value("${mpesa.consumer-secret}")
    private String consumerSecret;

    @Value("${mpesa.shortcode}")
    private String shortcode;

    @Value("${mpesa.passkey}")
    private String passkey;

    @Value("${mpesa.callback-url}")
    private String callbackUrl;

    @Value("${mpesa.api-url}")
    private String apiUrl;

    @Value("${mpesa.environment}")
    private String environment;

    // M-Pesa API endpoints
    public String getAuthUrl() {
        return apiUrl + "/oauth/v1/generate?grant_type=client_credentials";
    }

    public String getStkPushUrl() {
        return apiUrl + "/mpesa/stkpush/v1/processrequest";
    }

    public String getStkQueryUrl() {
        return apiUrl + "/mpesa/stkpushquery/v1/query";
    }
}