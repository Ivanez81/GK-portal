package ru.geekbrains.gkportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.geekbrains.gkportal.dto.SystemAccountToOwnerShip;
import ru.geekbrains.gkportal.entity.Communication;
import ru.geekbrains.gkportal.entity.Contact;
import ru.geekbrains.gkportal.entity.PropertyType;
import ru.geekbrains.gkportal.entity.questionnaire.QuestionnaireContactConfirm;
import ru.geekbrains.gkportal.util.MailMessageBuilder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MailService {

    private JavaMailSender sender;
    private MailMessageBuilder builder;

    private PropertyService propertyService;



    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public void setSender(JavaMailSender sender) {
        this.sender = sender;
    }

    @Autowired
    public void setBuilder(MailMessageBuilder builder) {
        this.builder = builder;
    }

    @Autowired
    public void setPropertyService(PropertyService propertyService) {
        this.propertyService = propertyService;
    }


    public void sendMail(List<String> emails, String subject, String text, boolean isHtml) {
        for (String mail : emails) {
            sendMail(mail, subject, text, isHtml);
        }
    }

    public boolean sendMail(String email, String subject, String text) {
        return sendMail(email, subject, text, true);
    }

    public boolean sendMail(String email, String subject, String text, boolean isHtml) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(email);
            helper.setText(text, isHtml);
            helper.setSubject(subject);
            helper.setFrom(propertyService.getPropertyValue("user_name", PropertyType.MAIL));
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }

        try {
            executorService.submit(() -> sender.send(message));
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendRegistrationMail(String email) {
        return sendMail(email, "Регистрация на сайте  ЖК Город", builder.buildRegistrationEmail());
    }

    public boolean sendRegistrationMail(Contact contact, Communication email) {
        String url = getCurentURL();
        return sendMail(email.getIdentify(),
                "Регистрация на сайте ЖК Город",
                builder.buildRegistrationEmail(contact.getLastName() + " " + contact.getFirstName() + " " + contact.getMiddleName() + " ",
                        url + "/confirmMail/" + email.getIdentify() + "/" + email.getConfirmCode()));
    }

    public boolean sendRegistrationMail(SystemAccountToOwnerShip systemAccout, Contact contact, QuestionnaireContactConfirm confirm) {
        String email = systemAccout.getEmail();
        String url = getCurentURL();
        return sendMail(email,
                "Регистрация в опросе на сайте ЖК Город",
                builder.buildRegistrationEmail(systemAccout.getAnswerResultDTO(), systemAccout.getLastName() + " " + systemAccout.getFirstName() + " " + systemAccout.getMiddleName() + " ",
                        url + "/confirmQuestion/" + contact.getUuid() + "/" + confirm.getConfirmCode()));
    }


    public String getCurentURL() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        req.getServerPort();
        return "http://" + req.getServerName() + ":" + req.getServerPort();

    }

}