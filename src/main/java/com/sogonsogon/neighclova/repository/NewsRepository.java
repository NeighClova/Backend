package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.News;
import com.sogonsogon.neighclova.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByPlaceId(Place place);
}
