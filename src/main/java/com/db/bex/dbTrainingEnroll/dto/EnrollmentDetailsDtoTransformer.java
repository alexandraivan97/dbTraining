package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.Enrollment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EnrollmentDetailsDtoTransformer {

    public EnrollmentDetailsDto transform(Enrollment enrollment){

        return EnrollmentDetailsDto.builder()
                .userEmail(enrollment.getUser().getMail())
                .comment(enrollment.getManagerComment())
                .trainingType(enrollment.getTrainingType())
                .urgencyType(enrollment.getUrgency())
                .department((enrollment.getUser().getDepartment().getDepartmentName()))
                .subdepartment(enrollment.getUser().getSubdepartment().getSubdepartmentName())
                .managerName(enrollment.getUser().getManager().getName())
                .build();
    }

    public List<EnrollmentDetailsDto> getUserSubordinates(List<Enrollment> enrollments) {
        return enrollments.stream().map(this::transform).collect(Collectors.toList());
    }
}
