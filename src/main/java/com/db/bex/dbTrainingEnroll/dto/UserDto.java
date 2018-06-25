package com.db.bex.dbTrainingEnroll.dto;

import com.db.bex.dbTrainingEnroll.entity.UserType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String name;
    private String mail;
    private UserType userType;
    private String lastLoginDate;
    private Long departmentId;
    private String departmentName;
    private Long subdepartmentId;
    private String subdepartmentName;
}
