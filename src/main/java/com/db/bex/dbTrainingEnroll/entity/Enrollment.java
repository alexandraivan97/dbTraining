package com.db.bex.dbTrainingEnroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="Enrollment")
public class Enrollment implements Serializable {

    @Id
    @Column(name = "enrollment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name = "training_id")
    private Training training;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnrollmentStatusType status;

    @Column(name = "manager_comment")
    private String managerComment;

    @Column(name = "pm_comment")
    private String pmComment;

    @Column(name = "training_type")
    @Enumerated(EnumType.STRING)
    private TrainingType trainingType;

    @Column(name = "urgency_type")
    @Enumerated(EnumType.STRING)
    private UrgencyType urgency;
}
