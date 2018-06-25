package com.db.bex.dbTrainingEnroll.service;

import com.db.bex.dbTrainingEnroll.dao.NotificationRepository;
import com.db.bex.dbTrainingEnroll.dao.UserRepository;
import com.db.bex.dbTrainingEnroll.dto.EmailDto;
import com.db.bex.dbTrainingEnroll.entity.Notification;
import com.db.bex.dbTrainingEnroll.entity.NotificationStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private UserRepository userRepository;
    private NotificationRepository notificationRepository;

    public NotificationService(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // used to get all notifications and make the new notifications old
    public List<Notification> getAllNotifications(EmailDto emailDto) {
        List<Notification> saveNotifications = notificationRepository
                .findAllByUserIdOrderByDateDesc(userRepository.findByMail(emailDto.getEmail()).getId());

        List<Notification> notifications = new ArrayList<>();

        for (Notification notification : saveNotifications) {
            notifications.add(new Notification(notification));
        }

        for (Notification notification : saveNotifications) {
            notification.setStatus(NotificationStatus.OLD);
        }
        notificationRepository.saveAll(saveNotifications);

        return notifications;
    }

    // used to get all the new notifications
    public List<Notification> getNewNotifications(EmailDto emailDto) {
        return notificationRepository.findAllByUserIdAndStatusOrderByDateDesc(userRepository.findByMail(emailDto.getEmail()).getId(),
                NotificationStatus.NEW);
    }
}
