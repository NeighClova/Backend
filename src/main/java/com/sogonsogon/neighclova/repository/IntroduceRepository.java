package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.Introduce;
import com.sogonsogon.neighclova.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntroduceRepository extends JpaRepository<Introduce, Long> {

    @Query("SELECT i FROM Introduce i WHERE i.placeId = :place ORDER BY i.createdAt DESC LIMIT 3")
    List<Introduce> findTop3ByPlace(@Param("place") Place place);
}
