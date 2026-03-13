package com.rwaknow.smartstore.controller;

import com.rwaknow.smartstore.service.MpesaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/mpesa")
@RequiredArgsConstructor
@Slf4j
public class MpesaCallbackController {

    private final MpesaService mpesaService;

    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("Received M-Pesa callback: {}", callbackData);

        try {
            mpesaService.handleCallback(callbackData);
            return ResponseEntity.ok(Map.of("ResultCode", "0", "ResultDesc", "Success"));
        } catch (Exception e) {
            log.error("Error processing M-Pesa callback", e);
            return ResponseEntity.ok(Map.of("ResultCode", "1", "ResultDesc", "Failed"));
        }
    }
}