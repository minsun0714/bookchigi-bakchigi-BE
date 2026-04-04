package com.bookchigi.auth.infrastructure;

import com.bookchigi.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken token, Long userId, long expireMillis);
    Optional<String> findUserId(RefreshToken token);
    void delete(RefreshToken token);
}