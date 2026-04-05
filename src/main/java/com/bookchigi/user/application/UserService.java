package com.bookchigi.user.application;

import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import com.bookchigi.user.presentation.dto.UserResponse;
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
        String picture = oAuth2User.getAttribute("picture");
        String OAUTH_PROVIDER = "GOOGLE";

        return userRepository.findActiveUserByEmailAndAuthProvider(email, "GOOGLE")
                .map(existingUser -> {
                    existingUser.updateProfileImage(picture);
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = User.createFromOAuth(email, name, picture, OAUTH_PROVIDER);
                    User savedUser = userRepository.save(newUser);

                    savedUser.updateNickname(savedUser.getName() + "#" + savedUser.getId());
                    return savedUser;
                });
    }

    @Transactional
    public UserResponse updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNicknameAndDeletedAtIsNull(nickname)) {
            throw new BusinessException(ErrorCode.NICKNAME_DUPLICATED);
        }

        user.updateNickname(nickname);
        return UserResponse.from(user);
    }
}