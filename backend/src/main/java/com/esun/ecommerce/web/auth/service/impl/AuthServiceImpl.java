package com.esun.ecommerce.web.auth.service.impl;

import com.esun.ecommerce.core.annotation.DbOperation;
import com.esun.ecommerce.core.entity.Admin;
import com.esun.ecommerce.core.entity.Member;
import com.esun.ecommerce.core.exception.BusinessException;
import com.esun.ecommerce.core.security.JwtUtil;
import com.esun.ecommerce.web.auth.dto.LoginRequestDto;
import com.esun.ecommerce.web.auth.dto.LoginResponseDto;
import com.esun.ecommerce.web.auth.dto.LoginResultDto;
import com.esun.ecommerce.web.auth.dto.RegisterRequestDto;
import com.esun.ecommerce.web.auth.repository.AdminRepository;
import com.esun.ecommerce.web.auth.repository.MemberRepository;
import com.esun.ecommerce.web.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    @DbOperation(action = "會員註冊")
    public void register(RegisterRequestDto dto) {
        if (memberRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("此帳號已被使用，請選擇其他帳號");
        }

        Member member = new Member();
        member.setUsername(dto.getUsername());
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        memberRepository.save(member);
    }

    @Override
    public LoginResultDto memberLogin(LoginRequestDto dto) {
        Member member = memberRepository.findByUsernameAndStatus(dto.getUsername(), (byte) 1)
                .orElseThrow(() -> new BusinessException("帳號或密碼錯誤"));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new BusinessException("帳號或密碼錯誤");
        }

        String token = jwtUtil.generateToken(member.getUsername(), "MEMBER", member.getMemberId());
        LoginResponseDto user = new LoginResponseDto("MEMBER", member.getMemberId(), member.getUsername());
        return new LoginResultDto(token, user);
    }

    @Override
    public LoginResultDto adminLogin(LoginRequestDto dto) {
        Admin admin = adminRepository.findByUsernameAndStatus(dto.getUsername(), (byte) 1)
                .orElseThrow(() -> new BusinessException("帳號或密碼錯誤"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException("帳號或密碼錯誤");
        }

        String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN", admin.getAdminId());
        LoginResponseDto user = new LoginResponseDto("ADMIN", admin.getAdminId(), admin.getUsername());
        return new LoginResultDto(token, user);
    }
}
