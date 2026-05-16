package com.esun.ecommerce.web.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {

    @NotBlank(message = "帳號不可為空")
    @Size(min = 3, max = 50, message = "帳號長度須為 3~50 字元")
    private String username;

    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, max = 100, message = "密碼長度須為 6~100 字元")
    private String password;

    @Email(message = "Email 格式不正確")
    private String email;

    @Size(max = 20, message = "電話號碼不可超過 20 字元")
    private String phone;
}
