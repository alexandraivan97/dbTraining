package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.dao.EnrollmentRepository;
import com.db.bex.dbTrainingEnroll.dao.TrainingRepository;
import com.db.bex.dbTrainingEnroll.dao.UserRepository;
import com.db.bex.dbTrainingEnroll.entity.Training;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TrainingDtoTransformer {

    TrainingRepository trainingRepository;
    EnrollmentRepository enrollmentRepository;
    UserRepository userRepository;
    private UserDtoTransformer userDtoTransformer;


    public List<TrainingDto> getTrainings(List<Training> training){
        return training.stream().map(this::transform).collect(Collectors.toList());
    }

    public TrainingDto transform(Training training){

       TrainingDto trainingDto = TrainingDto.builder()
                .id(training.getId())
                .name(training.getName().replaceAll("[\"]",""))
                .technology(training.getTechnology())
                .categoryType(training.getCategory())
                .acceptedUsers(enrollmentRepository.countAcceptedUsers(training.getId()).toString())
                .nrMax(training.getNrMax())
                .nrMin(training.getNrMin())
                .trainingResponsible(userDtoTransformer.transform(training.getTrainingResponsible()))
                .vendor(training.getVendor())
                .build();
       return trainingDto;
    }

}
