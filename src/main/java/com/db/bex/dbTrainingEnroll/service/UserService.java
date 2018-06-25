package com.db.bex.dbTrainingEnroll.service;

import com.db.bex.dbTrainingEnroll.dao.EnrollmentRepository;
import com.db.bex.dbTrainingEnroll.dao.NotificationRepository;
import com.db.bex.dbTrainingEnroll.dao.TrainingRepository;
import com.db.bex.dbTrainingEnroll.dao.UserRepository;
import com.db.bex.dbTrainingEnroll.dto.*;
import com.db.bex.dbTrainingEnroll.entity.*;
import com.db.bex.dbTrainingEnroll.exceptions.MissingDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private UserDtoTransformer userDtoTransformer;
    private EnrollmentDetailsDtoTransformer enrollmentDetailsDtoTransformer;
    private EnrollmentRepository enrollmentRepository;
    private TrainingRepository trainingRepository;
    private NotificationRepository notificationRepository;
    private EmailService emailService;

    @Autowired
    @Qualifier("dataSource1")
    private DataSource dataSource;

    public UserService(UserRepository userRepository, UserDtoTransformer userDtoTransformer, EnrollmentRepository enrollmentRepository, TrainingRepository trainingRepository,
                       NotificationRepository notificationRepository, EmailService emailService,
                       EnrollmentDetailsDtoTransformer enrollmentDetailsDtoTransformer) {
        this.userRepository = userRepository;
        this.userDtoTransformer = userDtoTransformer;
        this.enrollmentDetailsDtoTransformer = enrollmentDetailsDtoTransformer;
        this.enrollmentRepository = enrollmentRepository;
        this.trainingRepository = trainingRepository;
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    // used to get all subordinates of an manager
    public List<UserDto> findSubordinates(ManagerRequestDto managerRequestDto) throws MissingDataException {

        String email = managerRequestDto.getEmail();
        Long trainingId = managerRequestDto.getId();

        if(email == null || trainingId == null) {
            throw new MissingDataException("Email or id is null");
        }
        List<UserDto> userDtoList = new ArrayList<>();
        List<User> users = userRepository.findAllByManagerId(userRepository.findByMail(email).getId());
        if (users == null)
            throw new MissingDataException("Email or id is null");
        for (User user : users) {
            List<Enrollment> enrollments = enrollmentRepository.findAllByUserIdAndTrainingId(user.getId(), trainingId);
            System.out.println(user.getMail());
            System.out.println(enrollments);
            if (enrollments.isEmpty()) {
                userDtoList.add(userDtoTransformer.transform(user));
            }
        }
        return userDtoList;
    }

    // users waiting for SPOC approval
    public List<EnrollmentDetailsDto> findPendingUsers(ManagerTrainingRequestDto managerTrainingRequestDto) throws MissingDataException {

        String email = managerTrainingRequestDto.getEmail();
        Long idTraining = managerTrainingRequestDto.getId();

        Long idPm = userRepository.findByMail(email).getId();

        if(idPm == null) {
            throw new MissingDataException("Email does not exist");
        }

        return enrollmentDetailsDtoTransformer.getUserSubordinates(enrollmentRepository.findPendingUsers(idTraining, idPm));

    }

    // used for manager to put subordinates in PENDING
    public void savePendingSubordinates(ManagerResponseDto managerResponseDto) throws MissingDataException {

        Long trainingId = managerResponseDto.getTrainingId();
        List<EnrollmentDetailsDto> enrollmentDetailsDtos =  managerResponseDto.getEnrollmentDetailsDto();

        if(trainingId == null || managerResponseDto == null) {
            throw new MissingDataException("Id or list of users is null");
        }

        for(EnrollmentDetailsDto enrollmentDetailsDto : enrollmentDetailsDtos) {
        String userEmail = enrollmentDetailsDto.getUserEmail();
        String comment = enrollmentDetailsDto.getComment();
        TrainingType trainingType = enrollmentDetailsDto.getTrainingType();
        UrgencyType urgencyType = enrollmentDetailsDto.getUrgencyType();

        User user = userRepository.findByMail(userEmail);
        if(user == null) throw new MissingDataException("Id or list of users is null");

            Enrollment enrollment = enrollmentRepository.findByUserIdAndTrainingId(user.getId(),trainingId);
            if((enrollment != null))
                if(enrollment.getStatus() == EnrollmentStatusType.SELF_ENROLLED) {
                    enrollment.setStatus(EnrollmentStatusType.PENDING);
                    enrollment.setManagerComment(comment);
                    enrollment.setTrainingType(trainingType);
                    enrollment.setUrgency(urgencyType);
                    enrollmentRepository.save(enrollment);
            }
                else throw new MissingDataException("User already enrolled");
            else {
                Enrollment newEnrollment = new Enrollment();
                newEnrollment.setStatus(EnrollmentStatusType.PENDING);
                newEnrollment.setTraining(trainingRepository.findById(trainingId).get());
                newEnrollment.setUser(userRepository.findByMail(userEmail));
                newEnrollment.setManagerComment(comment);
                newEnrollment.setTrainingType(trainingType);
                newEnrollment.setUrgency(urgencyType);
                enrollmentRepository.save(newEnrollment);
            }}

    }

    // used for SPOC to approve or deny enrollments and send mail
    public void saveSubordinatesStatusAndSendEmail(List<UserStatusDto> userStatusDtos) throws MissingDataException {
        List<String> approvedUserEmails = new ArrayList<>();
        List<String> declinedUserEmails = new ArrayList<>();
        List<String> managerEmails = new ArrayList<>();
        Long trainingId = userStatusDtos.get(0).getIdTraining();
        Training training = trainingRepository.findById(trainingId).get();

        for(UserStatusDto u : userStatusDtos) {
            String mailUser = u.getMailUser();
            Long idTraining = u.getIdTraining();
            Long status = u.getStatus();
            User user = userRepository.findByMail(mailUser);
            Long id = user.getId();
            String pmComment = u.getComment();

            Enrollment enrollment = enrollmentRepository.findByUserIdAndTrainingId(id, idTraining);

            if( enrollment == null)
                throw new MissingDataException("Error");

            if (status == 1) {
                approvedUserEmails.add(mailUser);

                String managerMail = user.getManager().getMail();
                if (!managerEmails.contains(managerMail))
                    managerEmails.add(managerMail);
                enrollment.setStatus(EnrollmentStatusType.ACCEPTED);
                enrollment.setPmComment(pmComment);
                enrollmentRepository.save(enrollment);

                Notification notification = new Notification();
                notification.setStatus(NotificationStatus.NEW);
                notification.setType(NotifycationType.APPROVAL);
                notification.setMessage("You have been approved at " + training.getName());
                notification.setUser(user);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.HOUR_OF_DAY, 3);
                notification.setDate(dateFormat.format(cal.getTime()));
                notificationRepository.save(notification);
            }
            if (status == 0) {
                declinedUserEmails.add(mailUser);
                enrollmentRepository.delete(enrollment);

                Notification notification = new Notification();
                notification.setStatus(NotificationStatus.NEW);
                notification.setType(NotifycationType.DENIAL);
                notification.setMessage("You have been denied at " + training.getName() + " because: " + pmComment);
                notification.setUser(user);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.HOUR_OF_DAY, 3);
                notification.setDate(dateFormat.format(cal.getTime()));
                notificationRepository.save(notification);
            }
        }
        this.sendEmailToApprovedSubordinates(approvedUserEmails, trainingId);
        this.sendEmailToManagersWithSubordinates(managerEmails,trainingId);
        this.sendEmailToDeclinedSubordinates(declinedUserEmails, trainingId);
    }

    // used to send email to approved subordinates
    public void sendEmailToApprovedSubordinates(List<String> emails, Long trainingId){
        try {
            emailService.sendEmailToUsers(emails,"Congratulations! \n You've been approved at the training "
                            +  trainingRepository.findById(trainingId).get() + "."
                    ,"Training Approval");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // used to send email to declined subordinates
    public void sendEmailToDeclinedSubordinates(List<String> emails, Long trainingId){
        try{
            emailService.sendEmailToUsers(emails, "We are sorry to inform you that your enrollment at the training "
                    + trainingRepository.findById(trainingId).get() + " has been denied.", "Training Approval");
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    // used to send email to the manager
    public void sendEmailToManagersWithSubordinates(List<String> managers, Long trainingId){
        try {
            List<String> emails;
            for(String s : managers) {
                emails = enrollmentRepository.findApprovedSubordinatesAtTrainingId(userRepository.findByMail(s).getId(), trainingId);
                emailService.sendEmailToManager(s, "The following: \n\n " +
                        this.emailFormatter(emails) +  " \n have been approved at " +
                        trainingRepository.findById(trainingId).get() + ".", "Subordinates approved at training");
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // used to format email
    public String emailFormatter(List<String> emails){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            stringBuilder.append(userRepository.findByMail(email).getName());
            stringBuilder.append("(");
            stringBuilder.append(email);
            stringBuilder.append(")");
            if(i != emails.size()-1)
                stringBuilder.append(",");
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    // used by the user to self-enroll
    public void saveUserSelfEnroll(ManagerRequestDto managerRequestDto) throws MissingDataException {

        Long trainingId = managerRequestDto.getId();
        String userEmail = managerRequestDto.getEmail();
        Long userId = userRepository.findByMail(userEmail).getId();

        if(enrollmentRepository.findByUserIdAndTrainingId(userId, trainingId) != null)
            throw new MissingDataException("User already enrolled");

        Enrollment enrollment = new Enrollment();

        enrollment.setTraining(trainingRepository.findById(trainingId).get());
        enrollment.setUser(userRepository.findByMail(userEmail));
        enrollment.setStatus(EnrollmentStatusType.SELF_ENROLLED);

        enrollmentRepository.save(enrollment);
    }

    // used to get all subordinates of a manager self-enrolled at a training
    public List<UserDto> findSelfEnrolledSubordinates(ManagerRequestDto managerRequestDto) throws MissingDataException {
        String email = managerRequestDto.getEmail(); //manager email
        Long id = managerRequestDto.getId(); //training id

        User user= userRepository.findByMail(email);
        if(user == null)
            throw new MissingDataException("Manager email does exist");

        Long idManager = user.getId();

        if(email == null || id == null)
            throw new MissingDataException("Manager email or id null");

         if(userRepository.findUsersSelfEnrolled(idManager, id) == null)
             throw new MissingDataException("Manager does not have self enrolled users");

         return userDtoTransformer.getUserSubordinates1(userRepository.findUsersSelfEnrolled(idManager, id));
    }

    // used to get data about the user at login
    public UserDto getUserData (EmailDto emailDto) {
        UserDtoTransformer userDtoTransformer = new UserDtoTransformer();
        User user = userRepository.findByMail(emailDto.getEmail());
        user.setLastLoginDate(user.getCurrentLoginDate());
        user.setCurrentLoginDate(new Date());
        userRepository.save(user);
        return userDtoTransformer.transform(user);
    }

    // used to get gender statistics of accepted users
    public Integer[] getGenderCount () {
        Integer males = userRepository.countAcceptedUsersByGender(UserGenderType.MALE);
        Integer females = userRepository.countAcceptedUsersByGender(UserGenderType.FEMALE);
        Integer[] genders = {males, females};
        return genders;
    }

    // for test only
    public void addUser() {
        User user = new User();
        user.setName("Vasile");
        user.setMail("vasile2@gmail.com");
        user.setType(UserType.MANAGER);
        user.setPassword(new BCryptPasswordEncoder().encode("test"));
        user.setEnabled(true);
        user.setId(111111113L);
        user.setManager(null);
        user.setLastPasswordResetDate(new Date());
        userRepository.saveAndFlush(user);
    }
}
