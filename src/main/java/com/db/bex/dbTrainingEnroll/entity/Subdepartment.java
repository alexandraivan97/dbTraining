package com.db.bex.dbTrainingEnroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subdepartments")

public class Subdepartment {
    @Id
    @Column(name = "subdepartment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subdepartmentId;

    @Column(name = "subdepartment_name")
    private String subdepartmentName;

    @JsonIgnore
    @OneToMany(mappedBy="subdepartment" , fetch = FetchType.EAGER)
    private Set<User> subdepartmentSubordinates;
}
