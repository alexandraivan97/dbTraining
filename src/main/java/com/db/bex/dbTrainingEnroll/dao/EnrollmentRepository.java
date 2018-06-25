package com.db.bex.dbTrainingEnroll.dao;

import com.db.bex.dbTrainingEnroll.entity.Enrollment;
import com.db.bex.dbTrainingEnroll.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("select distinct t from Enrollment e " +
            "join e.training t " +
            "where e.status = 'PENDING' " +
            "and e.user.id IN " +
            "(select u.id from User u where u.manager.id IN" +
            "(select uu.id from User uu where uu.manager.id =:id))")
    List<Training> findTrainingsThatHavePendingParticipants(@Param("id") Long id);

    List<Enrollment> findAllByUserIdAndTrainingId(Long user_id, Long training_id);

    @Query("select e.training.id from Enrollment e" +
            " where e.user.id=?1 ")
    List<Long> findEnrolledTrainings(Long idUser);

    List<Enrollment> findAllByTrainingId(long id);

    Enrollment findByUserIdAndTrainingId(Long user_id, Long training_id);

    @Query("select count(e) from Enrollment e " +
            "where e.status = 'ACCEPTED'" +
            "and e.training.id =?1")
    Integer countAcceptedUsers(Long idTraining);

    @Query("select u.mail from Enrollment e join e.user u" +
            " where u.manager.id =?1" +
            " and e.status = 'ACCEPTED' and e.training.id =?2")
    List<String> findApprovedSubordinatesAtTrainingId(Long idManager, Long idTraining);

    @Query("select t from Enrollment e join e.training t where e.user.id =?1 and e.status = 'ACCEPTED'")
    List<Training> findAllByUserId(Long user_id);

    @Query("select e from Enrollment e " +
            "join e.user u where u.manager.id IN " +
            "(select uu.id from User uu where uu.manager.id=?2) " +
            "and e.status = 'PENDING'" +
            "and e.training.id =?1")
    List<Enrollment> findPendingUsers (Long idTraining, Long idPm);

}

