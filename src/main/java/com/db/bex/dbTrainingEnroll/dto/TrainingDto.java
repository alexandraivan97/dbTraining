package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.TrainingCategoryType;
import com.db.bex.dbTrainingEnroll.entity.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingDto {
    private Long id;
    private String name;
    private String date;
    private String duration;
    private String technology;
    private TrainingCategoryType categoryType;
    private String acceptedUsers;
    private Long nrMin;
    private Long nrMax;
    private UserDto trainingResponsible;
    private String vendor;
    private Double rating;
}
