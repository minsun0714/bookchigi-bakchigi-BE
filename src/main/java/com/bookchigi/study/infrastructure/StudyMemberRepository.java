package com.bookchigi.study.infrastructure;

import com.bookchigi.study.domain.StudyMember;
import com.bookchigi.study.domain.StudyRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    boolean existsByStudyIdAndUserId(Long studyId, Long userId);

    Optional<StudyMember> findByStudyIdAndRole(Long studyId, StudyRole role);
}
