package pcshop.pcshop.service;

public interface EmailService {
    void send(String to, String subject, String htmlBody);
}
