package com.rwaknow.smartstore.dto;

import com.rwaknow.smartstore.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthPayload {
    private String token;
    private User user;
}