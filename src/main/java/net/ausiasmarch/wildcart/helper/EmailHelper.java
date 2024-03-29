package net.ausiasmarch.wildcart.helper;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import net.ausiasmarch.wildcart.bean.EmailBean;
import net.ausiasmarch.wildcart.exception.CannotPerformOperationException;

public class EmailHelper {

    public static void sendEmail(EmailBean oEmailBean) throws Exception {

        String pass = "qwerty";

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", oEmailBean.getSender());
        properties.setProperty("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(oEmailBean.getSender()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(oEmailBean.getRecipient()));
            message.setSubject(oEmailBean.getSubject());
            message.setText(oEmailBean.getBody(), "utf-8", "html");
            //
            Transport oTransport = session.getTransport("smtp");
            oTransport.connect(oEmailBean.getSender(), pass);
            oTransport.sendMessage(message, message.getAllRecipients());
            oTransport.close();
        } catch (MessagingException mex) {
            throw new CannotPerformOperationException("Error sending email to " + oEmailBean.getRecipient());
        }
    }

    public static String getEmailTemplateRecover(String name, String url) {
        String t = "";
        t += "Hola, " + name + ".\n";
        t += "Hemos recibido una solicitud para restablecer la contraseña.\n";
        t += "El siguiente enlace web sirve para reestablecer la contraseña:\n";
        t += "   " + url + "\n";
        t += "Si no has sido tú quien lo ha solicitado, debes ignorar este mensaje.\n";
        t += "Por seguridad, nunca compartas este enlace con otras personas.\n";
        t += "Gracias,\n";
        t += "El equipo de WildCart\n";
        return t;
    }

    public static String getEmailTemplateValidate(String name, String url) {
        String t = "";
        t += "Hola, " + name + ".\n";
        t += "Hemos recibido una solicitud para establecer la contraseña.\n";
        t += "El siguiente enlace web sirve para establecer la contraseña:\n";
        t += "   " + url + "\n";
        t += "Si no has sido tú quien lo ha solicitado, debes ignorar este mensaje.\n";
        t += "Por seguridad, nunca compartas este enlace con otras personas.\n";
        t += "Gracias,\n";
        t += "El equipo de WildCart\n";
        return t;
    }

}
