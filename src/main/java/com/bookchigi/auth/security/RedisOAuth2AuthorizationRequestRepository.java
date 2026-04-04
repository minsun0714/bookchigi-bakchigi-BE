package com.bookchigi.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.time.Duration;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class RedisOAuth2AuthorizationRequestRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private static final String PREFIX = "oauth2:auth_request:";
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String STATE_PARAM = "state";

    private final StringRedisTemplate redisTemplate;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        String state = request.getParameter(STATE_PARAM);
        if (state == null) {
            return null;
        }
        String serialized = redisTemplate.opsForValue().get(PREFIX + state);
        if (serialized == null) {
            return null;
        }
        return deserialize(serialized);
    }

    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (authorizationRequest == null) {
            String state = request.getParameter(STATE_PARAM);
            if (state != null) {
                redisTemplate.delete(PREFIX + state);
            }
            return;
        }
        String state = authorizationRequest.getState();
        redisTemplate.opsForValue().set(PREFIX + state, serialize(authorizationRequest), TTL);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        OAuth2AuthorizationRequest authorizationRequest = loadAuthorizationRequest(request);
        if (authorizationRequest != null) {
            String state = authorizationRequest.getState();
            redisTemplate.delete(PREFIX + state);
        }
        return authorizationRequest;
    }

    private String serialize(OAuth2AuthorizationRequest request) {
        return Base64.getEncoder().encodeToString(SerializationUtils.serialize(request));
    }

    private OAuth2AuthorizationRequest deserialize(String serialized) {
        byte[] bytes = Base64.getDecoder().decode(serialized);
        return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(bytes);
    }
}
