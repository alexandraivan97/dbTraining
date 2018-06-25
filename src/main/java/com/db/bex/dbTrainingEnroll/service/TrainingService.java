package com.db.bex.dbTrainingEnroll.service;

import com.db.bex.dbTrainingEnroll.Recommender;
import com.db.bex.dbTrainingEnroll.dao.*;
import com.db.bex.dbTrainingEnroll.dto.*;
import com.db.bex.dbTrainingEnroll.entity.*;
import com.db.bex.dbTrainingEnroll.exceptions.MissingDataException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class TrainingService {

    private TrainingDtoTransformer trainingDtoTransformer;
    private UserRepository userRepository;
    private EnrollmentRepository enrollmentRepository;
    private TrainingRepository trainingRepository;
    @Autowired
    @Qualifier("dataSource1")
    private DataSource dataSource;
    private RatingRepository ratingRepository;
    private NotificationRepository notificationRepository;
    private UpdateRatingDtoTransformer updateRatingDtoTransformer;

    //get all trainings
    public Page<TrainingDto> findTrainings(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAllByOrderByStartDateDesc(pageable);
        List<Training> trainingList = trainingPage.getContent();
        List<TrainingDto> trainingDtoList = dateSetter(trainingList);
        trainingDtoList = this.setRating(trainingDtoList);
        Page<TrainingDto> page = new PageImpl<TrainingDto>(trainingDtoList);
        return page;
    }

    //get trainings for admin page
    public List<TrainingDto> findTrainings() {
        List<Training> trainings = trainingRepository.findAllByOrderByStartDateDesc();
        List<TrainingDto> trainingDtoList = dateSetter(trainings);
        return this.setRating(trainingDtoList);
    }

    //list of enrolled trainings for the current user
    public List<Long> enrolledTrainings(EmailDto emailDto) {

        User user = userRepository.findByMail(emailDto.getEmail());
        if (user == null)
            return null;
        Long id = user.getId();
        if(id == null) {
            throw new NullPointerException("Email does not exist");
        }
        return enrollmentRepository.findEnrolledTrainings(id);
    }

    //admin method to insert trainings
    public void insertTrainingList(List<TrainingDto> trainingDtos) throws MissingDataException {

        List<String> wrongTrainings = new ArrayList<>();

        for(TrainingDto trainingDto : trainingDtos)
            if((userRepository.findByMail(trainingDto.getTrainingResponsible().getMail()))==null) {
                wrongTrainings.add(trainingDto.getName());
                wrongTrainings.add(trainingDto.getTrainingResponsible().getMail());
            }

        if(wrongTrainings.size()!=0)
            throw new MissingDataException("You have to " +
                    "insert real user email as responsible: "+ wrongTrainings.toString()
                    + " !");

        for(TrainingDto trainingDto : trainingDtos) {
            Training training = new Training();
            trainingSetter(trainingDto, training);
            trainingRepository.save(training);
        }
    }

    //admin method to update trainings
    public void updateTrainingList(List<TrainingDto> trainingDtos) throws MissingDataException {

        List<String> wrongTrainings = new ArrayList<>();

        for(TrainingDto trainingDto : trainingDtos)
            if((userRepository.findByMail(trainingDto.getTrainingResponsible().getMail()))==null) {
                wrongTrainings.add(trainingDto.getName());
                wrongTrainings.add(trainingDto.getTrainingResponsible().getMail());
            }

        if(wrongTrainings.size()!=0)
            throw new MissingDataException("You have to " +
                    "insert real user email as responsible: "+ wrongTrainings.toString()
                    + " !");

        for(TrainingDto trainingDto : trainingDtos) {
            long id = trainingDto.getId();

            Training training = trainingRepository.findById(id);

            trainingSetter(trainingDto, training);

            trainingRepository.save(training);

            List<Enrollment> enrollments = enrollmentRepository.findAllByTrainingId(id);

            for (Enrollment enrollment : enrollments) {
                Notification notification = new Notification();
                notification.setStatus(NotificationStatus.NEW);
                notification.setType(NotifycationType.UPDATE);
                notification.setMessage(training.getName() + " has been modified!");
                notification.setUser(enrollment.getUser());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.HOUR_OF_DAY, 3);
                notification.setDate(dateFormat.format(cal.getTime()));
                notificationRepository.save(notification);
            }
        }
    }

    private void trainingSetter(TrainingDto trainingDto, Training training) {
        training.setName(trainingDto.getName());
        training.setTechnology(trainingDto.getTechnology());
        training.setCategory(trainingDto.getCategoryType());
        training.setNrMax(trainingDto.getNrMax());
        training.setNrMin(trainingDto.getNrMin());
        training.setTrainingResponsible(userRepository.findByMail(trainingDto.getTrainingResponsible().getMail()));
        training.setVendor(trainingDto.getVendor());
        splitDate(trainingDto, training);
    }

    //admin method to delete trainings
    public void deleteTrainingList(List<Long> trainingIdList) {
        for (Long trainingId : trainingIdList) {
            List<Enrollment> enrollments = enrollmentRepository.findAllByTrainingId(trainingId);
            Training training = trainingRepository.findById(trainingId).get();

            for (Enrollment enrollment : enrollments) {
                Notification notification = new Notification();
                notification.setStatus(NotificationStatus.NEW);
                notification.setType(NotifycationType.DELETE);
                notification.setMessage("Training " + training.getName() + " has been deleted!");
                notification.setUser(enrollment.getUser());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.HOUR_OF_DAY, 3);
                notification.setDate(dateFormat.format(cal.getTime()));
                notificationRepository.save(notification);
            }

            trainingRepository.deleteById(trainingId);
        }
    }

    //pending trainings for spoc
    public List<TrainingDto> findPendingTrainings(EmailDto email) {

        if(email == null) {
            throw new NullPointerException("Email is null");
        }

        User user = userRepository.findByMail(email.getEmail());
        if (user == null)
            return null;
        Long id = user.getId();
        if(id == null) {
            throw new NullPointerException("Email does not exist");
        }

        List<Training> pendingTrainings = enrollmentRepository.findTrainingsThatHavePendingParticipants(id);
        List<TrainingDto> trainingDtoList = dateSetter(pendingTrainings);
        return this.setRating(trainingDtoList);
    }

    public Integer countAcceptedUsers(Long idTraining) {
        return enrollmentRepository.countAcceptedUsers(idTraining);
    }


    public Integer[] countAcceptedTrainings() {
        Integer acceptedTech = trainingRepository.countAcceptedTechTraining();
        Integer acceptedSoft = trainingRepository.countAcceptedSoftTrainings();
        Integer[] techReport = {acceptedTech, acceptedSoft};
        return techReport;
    }

    public List<PopularityDto> countTopTechnicalAttendees() {
        return trainingRepository.countAcceptedTrainingsForEachCategory(TrainingCategoryType.TECHNICAL);
    }

    public List<PopularityDto> countTopSoftAttendees() {
        return trainingRepository.countAcceptedTrainingsForEachCategory(TrainingCategoryType.SOFT);
    }

    public List<PopularityDto> countTopAllAttendees() {
        return trainingRepository.countAcceptedTrainingsForAllCategories();
    }

    public List<MonthlyReportDto> findMonthlyReport() {
        List<MonthlyReportDto> list = trainingRepository.getMonthlyReport();
        for (MonthlyReportDto report : list) {
            Integer month = Integer.parseInt(report.getMonthNumber());
            report.setMonthString(getMonthName(month));
        }
        return list;
    }

    private String getMonthName(Integer month) {
        LocalDate localDate = LocalDate.of(1990, month, 1);
        return localDate.getMonth().name();
    }

    //used to get self enrolled trainings for user
    public List<TrainingDto> findEnrolledTrainings(EmailDto emailDto) {
        User manager = userRepository.findByMail(emailDto.getEmail());
        List<Training> trainingList = trainingRepository.findEnrolledTrainingsByManagerId(manager.getId());
        List<TrainingDto> trainingDtoList = dateSetter(trainingList);
        return this.setRating(trainingDtoList);
    }

    //used to get enrolled trainings for current user
    public List<TrainingDto> getAllApprovedTrainings(String userEmail) {
        List<TrainingDto> trainingDtoList = this.dateSetter(enrollmentRepository.findAllByUserId(
                userRepository.findByMail(userEmail).getId()));
        return this.setRating(trainingDtoList);
    }

    //used for recommendation
    public List<TrainingDto> findRecommendedTrainings(String email){
        Long userId = userRepository.findByMail(email).getId();
        Recommender recommender = new Recommender(trainingRepository,dataSource);
        List<Long> trainingsId = recommender.recommendTraining(userId,4);
        System.out.println(trainingsId.size());
        List<Training> trainings = null;
        if(!trainingsId.isEmpty())
        {
            trainings = new ArrayList<>();
            for(Long i : trainingsId) {
                if(trainingRepository.findById(i).isPresent())
                    trainings.add(trainingRepository.findById(i).get());
            }
        }
        List<TrainingDto> trainingDtoList = dateSetter(trainings);
        return this.setRating(trainingDtoList);
    }

    private List<TrainingDto> dateSetter(List<Training> trainingList) {
        List<TrainingDto> trainingDtoList = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat hourFormat = new SimpleDateFormat("HH:mm");
        TrainingDto trainingDto;

        for(Training training : trainingList) {
            trainingDto = trainingDtoTransformer.transform(training);

            String reportDateStart = dateFormat.format(training.getStartDate());
            String reportDateEnd = dateFormat.format(training.getEndDate());

            String reportStartHour = hourFormat.format(training.getStartDate());
            String reportEndHour = hourFormat.format(training.getEndDate());

            String[] startHourParts = reportStartHour.split(":");
            String startHour = startHourParts[0];
            String startMinute = startHourParts[1];

            String[] endHourParts = reportEndHour.split(":");
            String endHour = endHourParts[0];
            String endMinute = endHourParts[1];

            String[] startParts = reportDateStart.split("/");
            String startDay = startParts[0];
            String startMonth = startParts[1];
            String startYear = startParts[2];

            String[] endParts = reportDateEnd.split("/");
            String endDay = endParts[0];
            String endMonth = endParts[1];
            String endYear = endParts[2];

            if (Integer.parseInt(endYear) - Integer.parseInt(startYear) == 0) {
                if (Integer.parseInt(endMonth) - Integer.parseInt(startMonth) == 0) {
                    if (Integer.parseInt(endDay) - Integer.parseInt(startDay) == 0) {
                        trainingDto.setDate(reportDateStart);
                        if (Integer.parseInt(endMinute) - Integer.parseInt(startMinute) >= 0) {
                            int hours = Integer.parseInt(endHour) - Integer.parseInt(startHour);
                            int minutes = Integer.parseInt(endMinute) - Integer.parseInt(startMinute);
                            trainingDto.setDuration(hours + "h " +
                                    minutes + "m");
                        } else {
                            int hours = Integer.parseInt(endHour) - Integer.parseInt(startHour) - 1;
                            int minutes = 60 + Integer.parseInt(endMinute) - Integer.parseInt(startMinute);
                            trainingDto.setDuration(hours + "h " +
                                    minutes + "m");
                        }
                    } else {
                        trainingDto.setDate(reportDateStart + " - " + reportDateEnd);
                        trainingDto.setDuration("-1");
                    }
                } else {
                    trainingDto.setDate(reportDateStart + " - " + reportDateEnd);
                    trainingDto.setDuration("-1");
                }
            } else {
                trainingDto.setDate(reportDateStart + " - " + reportDateEnd);
                trainingDto.setDuration("-1");
            }

            trainingDtoList.add(trainingDto);
        }

        return trainingDtoList;
    }

    private void splitDate(TrainingDto trainingDto, Training training) {
        String endDate, startDate;
        String date = trainingDto.getDate();

        if(date.indexOf('-') >= 0) { //date contrains "-" , so we have stored in it the start date and the end date
            String[] dates = date.split("\\ - ");
            startDate = dates[0];
            endDate = dates[1];
        }
        else {
            startDate = date;
            endDate = date;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date startDate1 = formatter.parse(startDate);
            Date endDate1 = formatter.parse(endDate);
            training.setStartDate(startDate1);
            training.setEndDate(endDate1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List<TrainingDto> setRating(List<TrainingDto> trainingDtos) {
        for(TrainingDto i : trainingDtos) {
            i.setRating(ratingRepository.getRating(i.getId()));
        }
        return trainingDtos;
    }

    public Double updateRating(UpdateRatingDto updateRatingDto){
        Double updatedRating;
        Rating rating = ratingRepository.
                findByTrainingIdAndUserId(updateRatingDto.getTrainingId(),
                        userRepository.findByMail(updateRatingDto.getUserEmail()).getId());
        if(rating != null)
            rating.setRating(updateRatingDto.getRating());
        else{
            updatedRating = updateRatingDto.getRating();
            rating = new Rating();
            rating.setRating(updatedRating);
            rating.setTraining(trainingRepository.findById(updateRatingDto.getTrainingId()).get());
            rating.setUser(userRepository.findByMail(updateRatingDto.getUserEmail()));
        }
        ratingRepository.save(rating);
        updatedRating = ratingRepository.getRating(updateRatingDto.getTrainingId());
        return updatedRating;
    }
}
