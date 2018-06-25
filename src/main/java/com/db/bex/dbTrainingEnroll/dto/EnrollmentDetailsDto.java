package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.TrainingType;
import com.db.bex.dbTrainingEnroll.entity.UrgencyType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentDetailsDto {
    private String userEmail;
    private TrainingType trainingType;
    private UrgencyType urgencyType;
    private String comment;
    private String department;
    private String managerName;
    private String subdepartment;
}
