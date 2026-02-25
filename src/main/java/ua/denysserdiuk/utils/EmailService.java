package denysserdiuk.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, int verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Email Verification");
        message.setText("Your verification code is: " + verificationCode);

        mailSender.send(message);
        System.out.println("Verification email sent to " + toEmail);
    }

    public void sendContactEmail(String name, String email, String phone, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(fromEmail); // Sends to yourself
        message.setSubject("New Contact Form Submission");
        message.setText(buildEmailContent(name, email, phone, messageContent));

        mailSender.send(message);
        System.out.println("Contact email sent from " + email);
    }

    private String buildEmailContent(String name, String email, String phone, String messageContent) {
        return "Name: " + name + "\n"
                + "Email: " + email + "\n"
                + "Phone: " + phone + "\n"
                + "Message:\n" + messageContent;
    }
}