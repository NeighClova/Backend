package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.Certification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, String> {

    Certification findByEmail(@Param("email") String email);

    @Transactional
    void deleteByEmail(@Param("email") String email);
}
