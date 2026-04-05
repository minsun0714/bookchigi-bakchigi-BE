package com.bookchigi.user.presentation;

import com.bookchigi.auth.domain.CustomUserPrincipal;
import com.bookchigi.common.exception.BusinessException;
import com.bookchigi.common.exception.ErrorCode;
import com.bookchigi.user.domain.User;
import com.bookchigi.user.application.UserService;
import com.bookchigi.user.infrastructure.UserRepository;
import com.bookchigi.user.presentation.dto.NicknameUpdateRequest;
import com.bookchigi.user.presentation.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<UserResponse> updateNickname(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody NicknameUpdateRequest request
    ) {
        UserResponse response = userService.updateNickname(principal.getUserId(), request.nickname());
        return ResponseEntity.ok(response);
    }
}
