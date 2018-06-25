package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateRatingDtoTransformer {

    public UpdateRatingDto transform(Rating rating){
        return UpdateRatingDto.builder()
                .rating(rating.getRating())
                .build();
    }
}
