package com.esun.ecommerce.web.auth.controller;

import com.esun.ecommerce.core.util.ApiResponse;
import com.esun.ecommerce.web.auth.dto.LoginRequestDto;
import com.esun.ecommerce.web.auth.dto.LoginResponseDto;
import com.esun.ecommerce.web.auth.dto.LoginResultDto;
import com.esun.ecommerce.web.auth.dto.RegisterRequestDto;
import com.esun.ecommerce.web.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 前台：會員註冊
    @PostMapping("/front/auth/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequestDto dto) {
        authService.register(dto);
        return ApiResponse.success();
    }

    // 前台：會員登入
    @PostMapping("/front/auth/login")
    public ApiResponse<LoginResponseDto> memberLogin(@Valid @RequestBody LoginRequestDto dto) {
        LoginResultDto result = authService.memberLogin(dto);
        return ApiResponse.success(result.getUser(), result.getToken());
    }

    // 後台：管理員登入
    @PostMapping("/admin/auth/login")
    public ApiResponse<LoginResponseDto> adminLogin(@Valid @RequestBody LoginRequestDto dto) {
        LoginResultDto result = authService.adminLogin(dto);
        return ApiResponse.success(result.getUser(), result.getToken());
    }
}
