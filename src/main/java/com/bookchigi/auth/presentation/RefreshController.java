package com.bookchigi.auth.presentation;

import com.bookchigi.auth.application.RefreshTokenService;
import com.bookchigi.auth.domain.AccessToken;
import com.bookchigi.auth.domain.RefreshToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class RefreshController {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenValue,
            HttpServletResponse response
    ) {
        Optional<RefreshToken> refreshToken = RefreshToken.fromNullable(refreshTokenValue);

        if (refreshToken.isEmpty()) {
            log.info("refresh token is empty");
            clearCookie(response, "refresh_token", "/auth/refresh");
            return ResponseEntity.status(401).build();
        }

        Optional<AccessToken> newAccessToken = refreshTokenService.reissueAccessToken(refreshToken.get());

        if (newAccessToken.isEmpty()) {
            clearCookie(response, "refresh_token", "/auth/refresh");
            return ResponseEntity.status(401).build();
        }

        response.setHeader("Authorization", "Bearer " + newAccessToken.get().value());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenValue,
            HttpServletResponse response
    ) {
        RefreshToken.fromNullable(refreshTokenValue)
                .ifPresent(refreshTokenService::delete);

        clearCookie(response, "refresh_token", "/auth/refresh");
        return ResponseEntity.noContent().build();
    }

    private void clearCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path(path)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}