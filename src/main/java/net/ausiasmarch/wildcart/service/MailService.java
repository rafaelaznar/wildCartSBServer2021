
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
    
    private final JavaMailSender mailSender = null;    

    @Async
    void sendMail(EmailBean oEmailBean) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(oEmailBean.getSender());
            messageHelper.setTo(oEmailBean.getRecipient());
            messageHelper.setSubject(oEmailBean.getSubject());
            messageHelper.setText(oEmailBean.getBody());
        };
        try {
            mailSender.send(messagePreparator);            
        } catch (MailException e) {            
            throw new CannotPerformOperationException("Error al enviar email a " + oEmailBean.getRecipient());
        }
    }
}
