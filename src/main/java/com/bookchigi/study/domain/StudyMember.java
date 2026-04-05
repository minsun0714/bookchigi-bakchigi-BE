package com.bookchigi.study.domain;

import com.bookchigi.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "study_members", uniqueConstraints = {
        @UniqueConstraint(name = "uq_study_member", columnNames = {"study_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private StudyRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = Instant.now();
    }

    public static StudyMember createLeader(Study study, User user) {
        return StudyMember.builder()
                .study(study)
                .user(user)
                .role(StudyRole.LEADER)
                .build();
    }

    public static StudyMember createMember(Study study, User user) {
        return StudyMember.builder()
                .study(study)
                .user(user)
                .role(StudyRole.MEMBER)
                .build();
    }

    public static StudyMember createPending(Study study, User user) {
        return StudyMember.builder()
                .study(study)
                .user(user)
                .role(StudyRole.PENDING)
                .build();
    }

    public boolean isLeader() {
        return role == StudyRole.LEADER;
    }

    public boolean isPending() {
        return role == StudyRole.PENDING;
    }

    public void approve() {
        if (!isPending()) {
            throw new IllegalStateException("PENDING 상태에서만 승인할 수 있습니다.");
        }
        this.role = StudyRole.MEMBER;
    }

    public void promoteToLeader() {
        this.role = StudyRole.LEADER;
    }
}
