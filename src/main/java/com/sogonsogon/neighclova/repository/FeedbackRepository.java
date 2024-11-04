package com.sogonsogon.neighclova.repository;

import com.sogonsogon.neighclova.domain.Feedback;
import com.sogonsogon.neighclova.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    Feedback findTop1ByPlaceIdEqualsOrderByCreatedAtDesc(@Param("place") Place place);
}
