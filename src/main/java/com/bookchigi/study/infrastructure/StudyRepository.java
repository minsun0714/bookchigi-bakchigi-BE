package com.bookchigi.study.infrastructure;

import com.bookchigi.study.domain.Study;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Study s WHERE s.id = :studyId")
    Optional<Study> findByIdForUpdate(@Param("studyId") Long studyId);

    Page<Study> findByBookId(Long bookId, Pageable pageable);

    Page<Study> findByBookIsbnOrderByCreatedAtDesc(String isbn, Pageable pageable);
}
