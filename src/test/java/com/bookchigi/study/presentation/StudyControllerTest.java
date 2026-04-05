package com.bookchigi.study.presentation;

import com.bookchigi.auth.domain.CustomUserPrincipal;
import com.bookchigi.book.presentation.dto.PageResponse;
import com.bookchigi.study.application.StudyService;
import com.bookchigi.study.presentation.dto.StudyCreateRequest;
import com.bookchigi.study.presentation.dto.StudyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyService studyService;

    private UsernamePasswordAuthenticationToken createAuth(Long userId) {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                userId, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    @Test
    @DisplayName("POST /books/{isbn}/studies - 스터디를 생성한다")
    void createStudy() throws Exception {
        String isbn = "9791173576577";

        StudyCreateRequest request = new StudyCreateRequest(
                "자바 스터디", "자바를 공부합니다", 10,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 30),
                true
        );

        StudyResponse response = new StudyResponse(
                1L, "자바 스터디", "자바를 공부합니다", 10,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 30),
                true, "테스터#1", Instant.now()
        );

        given(studyService.create(eq(isbn), any(StudyCreateRequest.class), eq(1L)))
                .willReturn(response);

        mockMvc.perform(post("/books/{isbn}/studies", isbn)
                        .with(authentication(createAuth(1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("자바 스터디"))
                .andExpect(jsonPath("$.maxMembers").value(10));
    }

    @Test
    @DisplayName("POST /books/{isbn}/studies - 인증 없이 요청하면 401")
    void createStudyWithoutAuth() throws Exception {
        String isbn = "9791173576577";

        StudyCreateRequest request = new StudyCreateRequest(
                "자바 스터디", "자바를 공부합니다", 10,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 30),
                true
        );

        mockMvc.perform(post("/books/{isbn}/studies", isbn)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /books/{isbn}/studies - 책의 스터디 목록을 조회한다")
    void getStudies() throws Exception {
        String isbn = "9791173576577";

        StudyResponse studyResponse = new StudyResponse(
                1L, "자바 스터디", "자바를 공부합니다", 10,
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 4, 30),
                true, "테스터#1", Instant.now()
        );
        PageResponse<StudyResponse> pageResponse = new PageResponse<>(
                List.of(studyResponse), 0, 10, 1, 1
        );

        given(studyService.getStudiesByIsbn(isbn, 0, 10)).willReturn(pageResponse);

        mockMvc.perform(get("/books/{isbn}/studies", isbn)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("자바 스터디"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
