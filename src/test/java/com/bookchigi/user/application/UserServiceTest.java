package com.bookchigi.user.application;

import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.infrastructure.UserRepository;
import com.bookchigi.user.presentation.dto.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("닉네임을 수정할 수 있다")
    void updateNickname() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("test@gmail.com")
                .name("테스터")
                .nickname("테스터#1")
                .oauthProvider("GOOGLE")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNicknameAndDeletedAtIsNull("새닉네임")).willReturn(false);

        UserResponse response = userService.updateNickname(userId, "새닉네임");

        assertThat(response.nickname()).isEqualTo("새닉네임");
    }

    @Test
    @DisplayName("중복된 닉네임으로 수정하면 예외가 발생한다")
    void updateNicknameDuplicated() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("test@gmail.com")
                .name("테스터")
                .nickname("테스터#1")
                .oauthProvider("GOOGLE")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByNicknameAndDeletedAtIsNull("중복닉네임")).willReturn(true);

        assertThatThrownBy(() -> userService.updateNickname(userId, "중복닉네임"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NICKNAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 닉네임을 수정하면 예외가 발생한다")
    void updateNicknameUserNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateNickname(999L, "닉네임"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
