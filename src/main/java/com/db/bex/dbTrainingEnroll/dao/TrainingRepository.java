package com.db.bex.dbTrainingEnroll.dao;

import com.db.bex.dbTrainingEnroll.dto.MonthlyReportDto;
import com.db.bex.dbTrainingEnroll.dto.PopularityDto;
import com.db.bex.dbTrainingEnroll.dto.TrainingDto;
import com.db.bex.dbTrainingEnroll.entity.Training;
import com.db.bex.dbTrainingEnroll.entity.TrainingCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    Training findById(long id);

    //Page<Training> findAllByStartDateAsc(Pageable pageable);

    Page<Training> findAllByOrderByStartDateDesc(Pageable pageable);

    List<Training> findAllByOrderByStartDateDesc();

    @Query("SELECT count(e) FROM Enrollment e " +
            "join e.training t " +
            "where e.status = 'ACCEPTED' and t.category = 'SOFT'")
    Integer countAcceptedSoftTrainings();

    @Query("SELECT count(e) FROM Enrollment e " +
            "join e.training t " +
            "where e.status = 'ACCEPTED' and t.category = 'TECHNICAL'")
    Integer countAcceptedTechTraining();

    @Query("SELECT new com.db.bex.dbTrainingEnroll.dto.PopularityDto(" +
                    "t.technology, " +
                    "count(e.id)) " +
            "FROM Enrollment e " +
            "JOIN e.training t " +
            "WHERE t.category = ?1 and e.status = 'ACCEPTED' " +
            "GROUP BY t.technology")
    List<PopularityDto> countAcceptedTrainingsForEachCategory(TrainingCategoryType trainingCategoryType);

    @Query("SELECT new com.db.bex.dbTrainingEnroll.dto.PopularityDto(" +
            "t.technology, " +
            "count(e.id)) " +
            "FROM Enrollment e " +
            "JOIN e.training t " +
            "WHERE e.status = 'ACCEPTED' " +
            "GROUP BY t.technology")
    List<PopularityDto> countAcceptedTrainingsForAllCategories();

    @Query("SELECT new com.db.bex.dbTrainingEnroll.dto.MonthlyReportDto(" +
            "t.category, " +
            "substring(t.startDate,7,1), " +
            "count(e.id), " +
            "t.name) " +
            "FROM Enrollment e " +
            "JOIN e.training t " +
            "WHERE e.status = 'ACCEPTED' " +
            "GROUP BY t.category, substring(t.startDate,7,1)")
    List<MonthlyReportDto> getMonthlyReport();

    @Query("SELECT DISTINCT t FROM Enrollment e " +
            "join e.training t " +
            "join e.user u " +
            "WHERE u.manager.id = ?1 and e.status = 'SELF_ENROLLED'")
    List<Training> findEnrolledTrainingsByManagerId(Long managerId);
}