package com.esun.ecommerce.web.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    private String role;
    private Integer userId;
    private String username;
}
