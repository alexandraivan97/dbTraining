package com.db.bex.dbTrainingEnroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "training")
public class Training {

    @Id
    @Column(name = "training_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String technology;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingCategoryType category;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "nr_min", nullable = false)
    private Long nrMin;

    @Column(name = "nr_max", nullable = false)
    private Long nrMax;

    @JsonIgnore
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "training_responsible_id")
    private User trainingResponsible;

    @Column(name = "vendor")
    private String vendor;

    @JsonIgnore
    @OneToMany(mappedBy = "training",cascade ={CascadeType.ALL}, orphanRemoval = true)
    private Set<Enrollment> enrollments;

    @JsonIgnore
    @OneToMany(mappedBy = "training",cascade ={CascadeType.ALL}, orphanRemoval = true)
    private Set<Rating> ratings;

    @Override
    public String toString() {
        return name;
    }
}
