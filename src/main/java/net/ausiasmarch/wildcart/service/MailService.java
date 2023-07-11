package net.ausiasmarch.wildcart.service;

import net.ausiasmarch.wildcart.bean.EmailBean;
import net.ausiasmarch.wildcart.exception.CannotPerformOperationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private JavaMailSender oJavaMailSender = null;

    @Async
    void sendMail(EmailBean oEmailBean) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper oMimeMessageHelper = new MimeMessageHelper(mimeMessage);
            oMimeMessageHelper.setFrom(oEmailBean.getSender());
            oMimeMessageHelper.setTo(oEmailBean.getRecipient());
            oMimeMessageHelper.setSubject(oEmailBean.getSubject());
            oMimeMessageHelper.setText(oEmailBean.getBody());
        };
        try {
            oJavaMailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new CannotPerformOperationException("Error al enviar email a " + oEmailBean.getRecipient() + ": " + e.getMessage());
        }
    }
}
