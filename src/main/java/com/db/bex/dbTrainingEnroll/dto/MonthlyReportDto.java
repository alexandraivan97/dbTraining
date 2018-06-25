package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.TrainingCategoryType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyReportDto {
    private TrainingCategoryType category;
    private String monthNumber;
    private Long enrolled;
    private String monthString;
}
