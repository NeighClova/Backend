package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.Introduce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

}
