package pcshop.pcshop.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class SmtpEmailService implements EmailService{

    private final JavaMailSender mail;
    public SmtpEmailService(JavaMailSender mail) { this.mail = mail; }


    @Override
    public void send(String to, String subject, String htmlBody) {
        try{
            MimeMessage msg = mail.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg,"utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mail.send(msg);
        } catch (Exception e){
            throw new RuntimeException("Send mail failed",e);
        }
    }
}
