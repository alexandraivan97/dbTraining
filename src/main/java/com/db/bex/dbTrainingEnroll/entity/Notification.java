package com.db.bex.dbTrainingEnroll.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="Notification")
public class Notification {

    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 200)
    private String message;

    private NotifycationType type;
    private NotificationStatus status;
    private String date;

    public Notification(Notification notification) {
        this.id = notification.getId();
        this.user = notification.getUser();
        this.message = notification.getMessage();
        this.type = notification.getType();
        this.status = notification.getStatus();
        this.date = notification.getDate();
    }
}
