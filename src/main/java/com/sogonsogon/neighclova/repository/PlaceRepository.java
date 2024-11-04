package com.sogonsogon.neighclova.repository;


import com.sogonsogon.neighclova.domain.Place;
import com.sogonsogon.neighclova.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllByUserId(@Param("user") User user);
}
