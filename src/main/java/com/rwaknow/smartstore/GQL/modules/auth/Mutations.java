package com.rwaknow.smartstore.GQL.modules.auth;

import com.rwaknow.smartstore.dto.AuthPayload;
import com.rwaknow.smartstore.dto.LoginRequest;
import com.rwaknow.smartstore.dto.RegisterRequest;
import com.rwaknow.smartstore.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

/**
 * Auth module mutations — register and login
 */
@Controller("authMutations")
@RequiredArgsConstructor
public class Mutations {

    private final AuthService authService;

    @MutationMapping
    public AuthPayload register(
            @Argument String email,
            @Argument String password,
            @Argument String firstName,
            @Argument String lastName) {

        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setFirstName(firstName);
        request.setLastName(lastName);

        return authService.register(request);
    }

    @MutationMapping
    public AuthPayload login(
            @Argument String email,
            @Argument String password) {

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        return authService.login(request);
    }
}