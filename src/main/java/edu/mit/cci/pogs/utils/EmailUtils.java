package edu.mit.cci.pogs.utils;

import org.codemonkey.simplejavamail.MailException;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;
import org.codemonkey.simplejavamail.email.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import javax.mail.Message;

public class EmailUtils {
    public static void sendEmailToRecipient(String subject, String recepient, String html, String smtpHost,
                                            String smtpPort, String userName, String password
    ) {
        final Email email = new Email();

        try {
            email.setFromAddress("POGS admin", "noreply@pogs.info");
            email.setSubject(subject);
            email.setReplyToAddress("POGS admin", "noreply@pogs.info");
            email.addRecipient(null, recepient, Message.RecipientType.TO);
            email.setTextHTML(html);
            email.setText(html.replaceAll("\\<.*?\\>", ""));
            new Mailer(smtpHost, Integer.parseInt(smtpPort), userName, password,
                    TransportStrategy.SMTP_TLS)
                    .sendMail(email);

        } catch (MailException e) {

        }


    }
}
