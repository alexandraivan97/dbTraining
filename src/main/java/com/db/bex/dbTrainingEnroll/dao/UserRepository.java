package com.db.bex.dbTrainingEnroll.dao;

import com.db.bex.dbTrainingEnroll.entity.User;
import com.db.bex.dbTrainingEnroll.entity.UserGenderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    List<User> findAllByManagerId(long id);

    User findByMail(String email);

    @Query("select count(u.id) from Enrollment e " +
            "join e.user u " +
            "where e.status = 'ACCEPTED' " +
            "and u.gender = ?1")
    Integer countAcceptedUsersByGender(UserGenderType genderType);

    @Query("select u from Enrollment e " +
            "join e.user u where u.manager.id =?1 " +
            "and e.status = 'SELF_ENROLLED' " +
            "and e.training.id =?2")
    List<User> findUsersSelfEnrolled(Long idManager, Long id);
}
