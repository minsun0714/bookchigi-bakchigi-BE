package com.bookchigi.study.infrastructure;

import com.bookchigi.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Page<Study> findByBookId(Long bookId, Pageable pageable);

    Page<Study> findByBookIsbnOrderByCreatedAtDesc(String isbn, Pageable pageable);
}
