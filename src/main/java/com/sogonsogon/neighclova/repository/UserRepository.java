package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(@Param("email") String email);

    User findByUid(@Param("uid") String uid);

    boolean existsByEmail(@Param("email") String email);

    boolean existsByUid(@Param("uid") String uid);
}
