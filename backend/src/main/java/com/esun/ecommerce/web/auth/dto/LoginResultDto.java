package com.esun.ecommerce.web.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResultDto {

    private String token;
    private LoginResponseDto user;
}
