package com.bookchigi.user.application;

import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    @Transactional
    public User createIfNotExists(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String OAUTH_PROVIDER = "GOOGLE";

        return userRepository.findActiveUserByEmailAndAuthProvider(email, "GOOGLE")
                .orElseGet(() -> {
                    User newUser = User.createFromOAuth(email, name, OAUTH_PROVIDER);
                    User savedUser = userRepository.save(newUser);

                    savedUser.updateNickname(savedUser.getName() + "#" + savedUser.getId());
                    return savedUser;
                });
    }
}