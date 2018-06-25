package com.db.bex.dbTrainingEnroll.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRatingDto {
    private String userEmail;
    private Long trainingId;
    private Double rating;
}
