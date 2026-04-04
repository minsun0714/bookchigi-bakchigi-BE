package com.bookchigi.auth.application;

import com.bookchigi.auth.domain.AccessToken;
import com.bookchigi.auth.domain.RefreshToken;
import com.bookchigi.auth.domain.TokenPolicy;
import com.bookchigi.auth.infrastructure.JwtProvider;
import com.bookchigi.auth.infrastructure.RefreshTokenRepository;
import com.bookchigi.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final TokenPolicy tokenPolicy;

    public void save(RefreshToken refreshToken, Long userId, long expireMillis) {
        refreshTokenRepository.save(refreshToken, userId, expireMillis);
    }

    public Optional<String> getUserId(RefreshToken refreshToken) {
        return refreshTokenRepository.findUserId(refreshToken);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public Optional<AccessToken> reissueAccessToken(RefreshToken refreshToken) {
        Optional<String> userIdValue = getUserId(refreshToken);
        if (userIdValue.isEmpty()) {
            return Optional.empty();
        }

        long userId;
        try {
            userId = Long.parseLong(userIdValue.get());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        if (!userRepository.existsById(userId)) {
            delete(refreshToken);
            return Optional.empty();
        }

        AccessToken accessToken = jwtProvider.issueAccessToken(
                userId,
                tokenPolicy.accessTokenExpiresInMillis()
        );
        return Optional.of(accessToken);
    }
}