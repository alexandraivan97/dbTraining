package com.db.bex.dbTrainingEnroll.service;

import com.db.bex.dbTrainingEnroll.service.email.MailContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Component
public class EmailService {

    private JavaMailSender mailSender;
    @Autowired
    private MailContentBuilder mailContentBuilder;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // used to send email to users
    public void sendEmailToUsers(List<String> receivers, @NotEmpty String text, @NotEmpty String subject) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        for(String s : receivers)
            System.out.println(s);
        if(receivers.size() > 0) {
            helper.setTo(receivers.get(0));
            helper.setSubject(subject);
            if (receivers.size() > 1) {
                receivers.remove(0);
                helper.setBcc(receivers.toArray(new String[receivers.size()]));
            }

            String content = mailContentBuilder.build(text);
            helper.setText(content, true);
//            helper.setText(text);
            mailSender.send(mimeMessage);
        }
    }

    // used to send email to  manager
    public void sendEmailToManager(@NotEmpty String manager,@NotEmpty String text, @NotEmpty String subject) throws MessagingException {
        String content = mailContentBuilder.build(text);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(manager);
        helper.setSubject(subject);
        helper.setText(content);
        mailSender.send(mimeMessage);
    }
}
