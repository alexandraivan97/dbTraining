package com.db.bex.dbTrainingEnroll.controller;

import com.db.bex.dbTrainingEnroll.dto.*;
import com.db.bex.dbTrainingEnroll.exceptions.MissingDataException;
import com.db.bex.dbTrainingEnroll.service.TrainingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TrainingController {

    private TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    //get all trainings
    @GetMapping("/trainings")
    public List<TrainingDto> getTrainings(@PageableDefault(page = 0, value = Integer.MAX_VALUE) Pageable pageable){
        return trainingService.findTrainings(pageable).getContent();
    }

    //list of enrolled trainings for the current user
    @PostMapping("/enrolledTrainings")
    public List<Long> getEnrolledTrainings(@RequestBody EmailDto emailDto) {
        return trainingService.enrolledTrainings(emailDto);
    }

    //get trainings for admin page
    @GetMapping("/trainingsAdmin")
    public List<TrainingDto> getTrainings(){
        return trainingService.findTrainings();
    }

    //admin method to insert trainings
    @PostMapping("/insertTrainings")
    public void insertTrainings(@RequestBody List<TrainingDto> trainingDtos) throws MissingDataException {
        trainingService.insertTrainingList(trainingDtos);
    }

    //admin method to update trainings
    @PutMapping("/updateTrainings")
    public void updateTrainings(@RequestBody List<TrainingDto> trainingDtos) throws MissingDataException {
        trainingService.updateTrainingList(trainingDtos);
    }

    //admin method to delete trainings
    @DeleteMapping("/deleteTrainings")
    public void deleteTrainings(@RequestBody List<Long> trainingIdList) {
        trainingService.deleteTrainingList(trainingIdList);
    }

    //pending trainings for spoc
    @PostMapping("/pendingTrainings")
    public List<TrainingDto> getPendingTrainings(@RequestBody EmailDto email) {
        return trainingService.findPendingTrainings(email);
    }

    //used to get enrolled trainings for current user
    @PostMapping("/myTrainings")
    public List<TrainingDto> trainingList(@RequestBody EmailDto emailDto) {
        return trainingService.getAllApprovedTrainings(emailDto.getEmail());
    }

    //used to get self enrolled trainings for user
    @PostMapping("/selfEnrolledTrainings")
    public List<TrainingDto> enrolledTrainings(@RequestBody EmailDto emailDto) {
        return trainingService.findEnrolledTrainings(emailDto);
    }

    //used for recommendation
    //    used for recommendation
    @PostMapping("/recommend")
    public ResponseEntity<List<TrainingDto>> recommend(@RequestBody EmailDto emailDto) {
        List<TrainingDto> list = trainingService.findRecommendedTrainings(emailDto.getEmail());
        for(TrainingDto i : list)
            System.out.println(i.getName());
        return new ResponseEntity<>(list, HttpStatus.ACCEPTED);
    }

    //methods used for reports

    @GetMapping("/attendedTrainings")
    public Integer[] getAttendedTrainings() {
        return trainingService.countAcceptedTrainings();
    }

    @GetMapping("/topTechnicalAttendees")
    public List<PopularityDto> getTopTechincalAttendees() {
        return trainingService.countTopTechnicalAttendees();
    }

    @GetMapping("/topSoftAttendees")
    public List<PopularityDto> getTopSoftAttendees() {
        return trainingService.countTopSoftAttendees();
    }

    @GetMapping("/topAllAttendees")
    public List<PopularityDto> getTopAllAttendees() {
        return trainingService.countTopAllAttendees();
    }

    @GetMapping("/reportByMonth")
    public List<MonthlyReportDto> getReportByMonth() {
        return trainingService.findMonthlyReport();
    }

    @PostMapping("/updateRating")
    public Double updateTraining(@RequestBody UpdateRatingDto updateRatingDto) {
        return trainingService.updateRating(updateRatingDto);
    }

}
