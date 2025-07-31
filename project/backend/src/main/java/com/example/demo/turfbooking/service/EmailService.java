package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Send email verification during registration
    public void sendVerificationEmail(User user) {
        String subject = "Verify your email for Turf Booking";
        String senderName = "Mars Arena Turf Booking";
        String verifyURL = "http://localhost:3000/verify-email?token=" + user.getVerificationToken();

        String content = "<p>Hello <strong>" + user.getName() + "</strong>,</p>"
                + "<p>Thanks for registering. Click the link below to verify your email:</p>"
                + "<p><a href=\"" + verifyURL + "\">Verify Now</a></p>"
                + "<br><p>Regards,<br>Mars Arena Team</p>";

        sendHtmlEmail(user.getEmail(), subject, content, senderName);
    }

    // ✅ Send forgot password email
    public void sendResetPasswordEmail(User user) {
        String subject = "Reset your password - Turf Booking";
        String senderName = "Mars Arena Turf Booking";
        String resetURL = "http://localhost:3000/reset-password?token=" + user.getResetPasswordToken();

        String content = "<p>Hello <strong>" + user.getName() + "</strong>,</p>"
                + "<p>You requested a password reset. Click below to reset your password:</p>"
                + "<p><a href=\"" + resetURL + "\">Reset Password</a></p>"
                + "<br><p>If you didn't request this, ignore this email.</p>"
                + "<p>Regards,<br>Mars Arena Team</p>";

        sendHtmlEmail(user.getEmail(), subject, content, senderName);
    }

    // ✅ Send booking confirmation email after successful payment
    public void sendBookingConfirmationEmail(String toEmail, String userName, String turfName, String date, String slot, String price) {
        String subject = "✅ Turf Booking Confirmed!";
        String senderName = "Mars Arena Turf Booking";

        String content = "<p>Hello <strong>" + userName + "</strong>,</p>"
                + "<p>Your turf booking is confirmed! 🎉</p>"
                + "<table style='border-collapse: collapse; margin-top: 10px;'>"
                + "<tr><td><b>Turf:</b></td><td>" + turfName + "</td></tr>"
                + "<tr><td><b>Date:</b></td><td>" + date + "</td></tr>"
                + "<tr><td><b>Slot:</b></td><td>" + slot + "</td></tr>"
                + "<tr><td><b>Price:</b></td><td>₹" + price + "</td></tr>"
                + "</table>"
                + "<p>📧 This is your confirmation email. No further action is needed.</p>"
                + "<br><p>Regards,<br><strong>Mars Arena Team</strong></p>";

        sendHtmlEmail(toEmail, subject, content, senderName);
    }

    // ✅ Shared method to send HTML emails
    public void sendHtmlEmail(String to, String subject, String htmlContent, String senderName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("monidhoni0007@gmail.com", senderName); // 🔁 Replace with your actual sender email
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Backup plain email method (optional use)
    public void sendEmail(String to, String subject, String content) {
        sendHtmlEmail(to, subject, content, "Mars Arena Turf Booking");
    }
}
