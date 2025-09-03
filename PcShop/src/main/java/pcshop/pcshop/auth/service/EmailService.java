package pcshop.pcshop.auth.dto.service;

public interface EmailService {
    void send(String to, String subject, String htmlBody);
}
