package com.esun.ecommerce.core.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {

    private final Integer userId;
    private final String username;
    private final String role;
}
