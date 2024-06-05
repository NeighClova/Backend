package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.News;
import com.sogonsogon.neighclova.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.placeId = :place ORDER BY n.createdAt DESC")
    List<News> findAllByPlaceId(Place place);

    @Query("SELECT n.createdAt FROM News n WHERE n.placeId = :place ORDER BY n.createdAt DESC LIMIT 1")
    LocalDateTime findCreatedAtByPlaceIdEqualsOrderByCreatedAtDesc(Place place);
}
