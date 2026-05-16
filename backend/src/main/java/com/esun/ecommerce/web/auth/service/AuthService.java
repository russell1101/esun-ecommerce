package com.esun.ecommerce.web.auth.service;

import com.esun.ecommerce.web.auth.dto.LoginRequestDto;
import com.esun.ecommerce.web.auth.dto.LoginResultDto;
import com.esun.ecommerce.web.auth.dto.RegisterRequestDto;

public interface AuthService {

    void register(RegisterRequestDto dto);

    LoginResultDto memberLogin(LoginRequestDto dto);

    LoginResultDto adminLogin(LoginRequestDto dto);
}
