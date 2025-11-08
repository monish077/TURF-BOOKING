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

    // Frontend URL for email links
    private static final String FRONTEND_URL = "https://turf-booking-seven.vercel.app";
    // private static final String FRONTEND_URL = "http://localhost:3000"; // For local testing

    // Sender information
    private static final String SENDER_EMAIL = "monidhoni0007@gmail.com";
    private static final String SENDER_NAME = "Mars Arena Turf Booking";

    /**
     * Send verification email during user registration
     */
    public void sendVerificationEmail(User user) {
        String subject = "Verify your email for Turf Booking";
        String verifyURL = FRONTEND_URL + "/verify-email?token=" + user.getVerificationToken();

        String content = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Thanks for registering. Click the link below to verify your email:</p>
            <p><a href="%s">Verify Now</a></p>
            <br><p>Regards,<br><strong>Mars Arena Team</strong></p>
            """.formatted(user.getName(), verifyURL);

        sendHtmlEmail(user.getEmail(), subject, content);
    }

    /**
     * Send password reset email
     */
    public void sendResetPasswordEmail(User user) {
        String subject = "Reset your password - Turf Booking";
        String resetURL = FRONTEND_URL + "/reset-password?token=" + user.getResetPasswordToken();

        String content = """
            <p>Hello <strong>%s</strong>,</p>
            <p>You requested a password reset. Click below to reset your password:</p>
            <p><a href="%s">Reset Password</a></p>
            <br><p>If you didn't request this, you can safely ignore this email.</p>
            <p>Regards,<br><strong>Mars Arena Team</strong></p>
            """.formatted(user.getName(), resetURL);

        sendHtmlEmail(user.getEmail(), subject, content);
    }

    /**
     * Send booking confirmation email after payment
     */
    public void sendBookingConfirmationEmail(
            String toEmail,
            String userName,
            String turfName,
            String date,
            String slot,
            String price
    ) {
        String subject = "✅ Turf Booking Confirmed!";

        String content = """
            <p>Hello <strong>%s</strong>,</p>
            <p>Your turf booking is confirmed! 🎉</p>
            <table style='border-collapse: collapse; margin-top: 10px;'>
                <tr><td><b>Turf:</b></td><td>%s</td></tr>
                <tr><td><b>Date:</b></td><td>%s</td></tr>
                <tr><td><b>Slot:</b></td><td>%s</td></tr>
                <tr><td><b>Price:</b></td><td>₹%s</td></tr>
            </table>
            <p>📧 This is your confirmation email. No further action is needed.</p>
            <br><p>Regards,<br><strong>Mars Arena Team</strong></p>
            """.formatted(userName, turfName, date, slot, price);

        sendHtmlEmail(toEmail, subject, content);
    }

    /**
     * Shared method to send HTML emails
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email to " + to, e);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error while sending email: " + e.getMessage());
            throw new RuntimeException("Unexpected error while sending email", e);
        }
    }

    /**
     * Backup plain method to send email
     */
    public void sendEmail(String to, String subject, String content) {
        sendHtmlEmail(to, subject, content);
    }
}
