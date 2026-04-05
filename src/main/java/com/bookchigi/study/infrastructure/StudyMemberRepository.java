package com.bookchigi.study.infrastructure;

import com.bookchigi.study.domain.StudyMember;
import com.bookchigi.study.domain.StudyRole;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    boolean existsByStudyIdAndUserId(Long studyId, Long userId);

    Optional<StudyMember> findByStudyIdAndUserId(Long studyId, Long userId);

    Optional<StudyMember> findByStudyIdAndRole(Long studyId, StudyRole role);

    List<StudyMember> findByStudyId(Long studyId);

    List<StudyMember> findByStudyIdAndRole(Long studyId, StudyRole role, Sort sort);

    long countByStudyId(Long studyId);

    long countByStudyIdAndRoleNot(Long studyId, StudyRole role);

    Page<StudyMember> findByUserIdAndRoleOrderByJoinedAtDesc(Long userId, StudyRole role, Pageable pageable);

    void deleteAllByStudyId(Long studyId);
}
