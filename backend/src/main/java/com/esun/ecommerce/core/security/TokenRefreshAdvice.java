package com.esun.ecommerce.core.security;

import com.esun.ecommerce.core.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 滑動 Token
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class TokenRefreshAdvice implements ResponseBodyAdvice<ApiResponse<?>> {

    private final JwtUtil jwtUtil;

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public ApiResponse<?> beforeBodyWrite(ApiResponse<?> body,
                                          MethodParameter returnType,
                                          MediaType selectedContentType,
                                          Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                          ServerHttpRequest request,
                                          ServerHttpResponse response) {
        if (body == null) return null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            // 產生新JWT（expiration重新計算）
            String refreshedToken = jwtUtil.generateToken(
                    principal.getUsername(),
                    principal.getRole(),
                    principal.getUserId()
            );
            body.setToken(refreshedToken);
        }

        return body;
    }
}
