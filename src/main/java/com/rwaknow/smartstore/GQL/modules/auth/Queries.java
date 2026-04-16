package com.rwaknow.smartstore.GQL.modules.auth;

import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

/**
 * Auth module queries
 */
@Controller("authQueries")
@RequiredArgsConstructor
public class Queries {

    @QueryMapping
    public User me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userDetails.getUser();
    }
}