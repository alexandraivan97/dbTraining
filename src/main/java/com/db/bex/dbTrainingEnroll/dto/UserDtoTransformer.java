package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.dao.EnrollmentRepository;
import com.db.bex.dbTrainingEnroll.dao.TrainingRepository;
import com.db.bex.dbTrainingEnroll.entity.Enrollment;
import com.db.bex.dbTrainingEnroll.entity.User;
import com.db.bex.dbTrainingEnroll.dao.UserRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Component
public class UserDtoTransformer {

    private UserRepository userRepository;

    private TrainingRepository trainingRepository;

    private EnrollmentRepository enrollmentRepository;

    public UserDtoTransformer(UserRepository userRepository, TrainingRepository trainingRepository, EnrollmentRepository enrollmentRepository) {
        this.userRepository = userRepository;
        this.trainingRepository = trainingRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public UserDto transform(User user){

        String userLastLoginDate = "Never";

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        if(user.getLastLoginDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(user.getLastLoginDate());
            cal.add(Calendar.HOUR_OF_DAY, 3);
            userLastLoginDate = dateFormat.format(cal.getTime());
        }

        return UserDto.builder()
                .mail(user.getMail())
                .name(user.getName())
                .userType(user.getType())
                .lastLoginDate(userLastLoginDate)
                .build();
    }

    public List<UserDto> getUserSubordinates1(List<User> user) {
       return user.stream().map(this::transform).collect(Collectors.toList());
    }

    public List<UserDto> filterUsers(List<UserDto> listDTO, long id){
        List<UserDto> newList = new ArrayList<>();
        for(UserDto i: listDTO){
            List<Enrollment> list = enrollmentRepository.findAllByTrainingId(id);
            System.out.println(list);
            int ok = 0;
            for(Enrollment j : list)
                if (listDTO.contains(this.transform(j.getUser())))
                    ok = 1;
            if(ok == 0)
                newList.add(i);
        }
        System.out.println(newList);
        return newList;
    }
}
