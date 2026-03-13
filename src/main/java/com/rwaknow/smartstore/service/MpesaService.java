package com.rwaknow.smartstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rwaknow.smartstore.config.MpesaConfig;
import com.rwaknow.smartstore.dto.MpesaPaymentRequest;
import com.rwaknow.smartstore.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpesaService {

    private final MpesaConfig mpesaConfig;
    private final OrderService orderService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> initiateStkPush(MpesaPaymentRequest request) {
        try {
            // Get access token
            String accessToken = getAccessToken();

            // Get order
            Order order = orderService.getOrderById(request.getOrderId());

            // Prepare STK Push request
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String password = generatePassword(timestamp);

            Map<String, Object> stkPushRequest = new HashMap<>();
            stkPushRequest.put("BusinessShortCode", mpesaConfig.getShortcode());
            stkPushRequest.put("Password", password);
            stkPushRequest.put("Timestamp", timestamp);
            stkPushRequest.put("TransactionType", "CustomerPayBillOnline");
            stkPushRequest.put("Amount", request.getAmount().intValue());
            stkPushRequest.put("PartyA", request.getPhoneNumber());
            stkPushRequest.put("PartyB", mpesaConfig.getShortcode());
            stkPushRequest.put("PhoneNumber", request.getPhoneNumber());
            stkPushRequest.put("CallBackURL", mpesaConfig.getCallbackUrl());
            stkPushRequest.put("AccountReference", "Order-" + order.getId());
            stkPushRequest.put("TransactionDesc", "Payment for Order #" + order.getId());

            // Send STK Push
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(stkPushRequest, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getStkPushUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("checkoutRequestId", responseBody.get("CheckoutRequestID").asText());
            result.put("merchantRequestId", responseBody.get("MerchantRequestID").asText());
            result.put("responseCode", responseBody.get("ResponseCode").asText());
            result.put("responseDescription", responseBody.get("ResponseDescription").asText());

            return result;

        } catch (Exception e) {
            log.error("STK Push failed", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Payment initiation failed: " + e.getMessage());
            return error;
        }
    }

    private String getAccessToken() {
        try {
            String auth = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    mpesaConfig.getAuthUrl(),
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode responseBody = objectMapper.readTree(response.getBody());
            return responseBody.get("access_token").asText();

        } catch (Exception e) {
            log.error("Failed to get M-Pesa access token", e);
            throw new RuntimeException("Failed to authenticate with M-Pesa: " + e.getMessage());
        }
    }

    private String generatePassword(String timestamp) {
        String str = mpesaConfig.getShortcode() + mpesaConfig.getPasskey() + timestamp;
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    public void handleCallback(Map<String, Object> callbackData) {
        // Process M-Pesa callback
        // This will be called from the controller
        log.info("M-Pesa callback received: {}", callbackData);
        // TODO: Parse callback and update order payment status
    }
}