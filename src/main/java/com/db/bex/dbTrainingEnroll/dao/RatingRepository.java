package com.db.bex.dbTrainingEnroll.dao;

import com.db.bex.dbTrainingEnroll.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT avg(r.rating) FROM Rating r " +
            "WHERE r.training.id = ?1")
    Double getRating(Long trainingId);

    Rating findByTrainingId(Long training_id);
    Rating findByTrainingIdAndUserId(Long training_id, Long user_id);
}
