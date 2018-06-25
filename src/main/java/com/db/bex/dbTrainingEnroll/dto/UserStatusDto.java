package com.db.bex.dbTrainingEnroll.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatusDto {
    private String mailUser;
    private Long idTraining;
    private Long status;
    private String comment;
}
