package pcshop.pcshop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("dev-console")
public class ConsoleEmailService implements EmailService{
    @Override
    public void send(String to, String subject, String htmlBody) {
        log.info("[DEV MAIL] to-{} | subject={}\n{}", to, subject, htmlBody);
    }
}
