package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Frontend URL for email links
    private static final String FRONTEND_URL = "https://turf-booking-seven.vercel.app";

    // Sender information
    private static final String SENDER_EMAIL = "monidhoni0007@gmail.com";
    private static final String SENDER_NAME = "Mars Arena Turf Booking";

    /**
     * Send verification email during user registration
     */
    public void sendVerificationEmail(User user) {
        System.out.println("📧 [EMAIL SERVICE] Starting sendVerificationEmail for: " + user.getEmail());
        
        String subject = "Verify your email for Turf Booking";
        String verifyURL = FRONTEND_URL + "/verify-email?token=" + user.getVerificationToken();

        System.out.println("📧 [EMAIL SERVICE] Verification token: " + user.getVerificationToken());
        System.out.println("📧 [EMAIL SERVICE] Verification URL: " + verifyURL);

        // Simple HTML content without complex formatting
        String content = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<title>Verify Your Email</title>" +
            "</head>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px;\">" +
            "<div style=\"max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 20px; border-radius: 10px;\">" +
            "<div style=\"background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;\">" +
            "<h1>Welcome to Mars Arena!</h1>" +
            "</div>" +
            "<div style=\"background: white; padding: 20px; border-radius: 0 0 10px 10px;\">" +
            "<h2>Hello " + user.getName() + ",</h2>" +
            "<p>Thanks for registering with Mars Arena Turf Booking. Please verify your email address to complete your registration.</p>" +
            "<p style=\"text-align: center;\">" +
            "<a href=\"" + verifyURL + "\" style=\"display: inline-block; padding: 12px 24px; background: #667eea; color: white; text-decoration: none; border-radius: 5px; margin: 15px 0;\">Verify Your Email</a>" +
            "</p>" +
            "<p>If the button doesn't work, copy and paste this link in your browser:</p>" +
            "<p style=\"word-break: break-all; color: #667eea;\">" + verifyURL + "</p>" +
            "<p>This verification link will expire in 24 hours.</p>" +
            "</div>" +
            "<div style=\"text-align: center; margin-top: 20px; padding: 10px; color: #666;\">" +
            "<p>Regards,<br><strong>Mars Arena Team</strong></p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>";

        sendHtmlEmail(user.getEmail(), subject, content);
        System.out.println("📧 [EMAIL SERVICE] Completed sendVerificationEmail for: " + user.getEmail());
    }

    /**
     * Send password reset email
     */
    public void sendResetPasswordEmail(User user) {
        System.out.println("📧 [EMAIL SERVICE] Starting sendResetPasswordEmail for: " + user.getEmail());
        
        String subject = "Reset Your Password - Mars Arena";
        String resetURL = FRONTEND_URL + "/reset-password?token=" + user.getResetPasswordToken();

        System.out.println("📧 [EMAIL SERVICE] Reset token: " + user.getResetPasswordToken());

        // Simple HTML content
        String content = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<title>Password Reset</title>" +
            "</head>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px;\">" +
            "<div style=\"max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 20px; border-radius: 10px;\">" +
            "<div style=\"background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;\">" +
            "<h1>Password Reset Request</h1>" +
            "</div>" +
            "<div style=\"background: white; padding: 20px; border-radius: 0 0 10px 10px;\">" +
            "<h2>Hello " + user.getName() + ",</h2>" +
            "<p>You requested to reset your password. Click the button below to create a new password:</p>" +
            "<p style=\"text-align: center;\">" +
            "<a href=\"" + resetURL + "\" style=\"display: inline-block; padding: 12px 24px; background: #f5576c; color: white; text-decoration: none; border-radius: 5px; margin: 15px 0;\">Reset Password</a>" +
            "</p>" +
            "<p>If the button doesn't work, copy and paste this link in your browser:</p>" +
            "<p style=\"word-break: break-all; color: #f5576c;\">" + resetURL + "</p>" +
            "<div style=\"background: #fff3cd; border: 1px solid #ffeaa7; padding: 10px; border-radius: 5px; margin: 15px 0;\">" +
            "<p><strong>⚠️ Important:</strong> This link will expire in 1 hour for security reasons.</p>" +
            "</div>" +
            "<p>If you didn't request this password reset, please ignore this email and your password will remain unchanged.</p>" +
            "</div>" +
            "<div style=\"text-align: center; margin-top: 20px; padding: 10px; color: #666;\">" +
            "<p>Regards,<br><strong>Mars Arena Team</strong></p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>";

        sendHtmlEmail(user.getEmail(), subject, content);
        System.out.println("📧 [EMAIL SERVICE] Completed sendResetPasswordEmail for: " + user.getEmail());
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
        System.out.println("📧 [EMAIL SERVICE] Starting sendBookingConfirmationEmail for: " + toEmail);
        
        String subject = "✅ Your Turf Booking is Confirmed! - Mars Arena";

        String content = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<title>Booking Confirmed</title>" +
            "</head>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px;\">" +
            "<div style=\"max-width: 600px; margin: 0 auto; background: #f9f9f9; padding: 20px; border-radius: 10px;\">" +
            "<div style=\"background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0;\">" +
            "<h1>🎉 Booking Confirmed!</h1>" +
            "</div>" +
            "<div style=\"background: white; padding: 20px; border-radius: 0 0 10px 10px;\">" +
            "<h2>Hello " + userName + ",</h2>" +
            "<p style=\"color: #28a745; font-weight: bold;\">Your turf booking has been successfully confirmed! We're excited to have you play with us.</p>" +
            "<div style=\"background: white; padding: 15px; border-radius: 8px; border-left: 4px solid #4facfe; margin: 15px 0;\">" +
            "<h3>📋 Booking Details</h3>" +
            "<div style=\"display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee;\">" +
            "<span style=\"font-weight: bold; color: #555;\">Turf Name:</span>" +
            "<span style=\"color: #333;\">" + turfName + "</span>" +
            "</div>" +
            "<div style=\"display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee;\">" +
            "<span style=\"font-weight: bold; color: #555;\">Date:</span>" +
            "<span style=\"color: #333;\">" + date + "</span>" +
            "</div>" +
            "<div style=\"display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #eee;\">" +
            "<span style=\"font-weight: bold; color: #555;\">Time Slot:</span>" +
            "<span style=\"color: #333;\">" + slot + "</span>" +
            "</div>" +
            "<div style=\"display: flex; justify-content: space-between; padding: 8px 0;\">" +
            "<span style=\"font-weight: bold; color: #555;\">Amount Paid:</span>" +
            "<span style=\"color: #333;\">₹" + price + "</span>" +
            "</div>" +
            "</div>" +
            "<p><strong>📍 Location Instructions:</strong></p>" +
            "<p>Please arrive 15 minutes before your scheduled time at the turf location.</p>" +
            "<p><strong>📞 Need Help?</strong></p>" +
            "<p>If you have any questions or need to make changes to your booking, please contact us at:</p>" +
            "<p>📧 support@marsarena.com | 📞 +91-9876543210</p>" +
            "<p>We look forward to seeing you at the turf! ⚽</p>" +
            "</div>" +
            "<div style=\"text-align: center; margin-top: 20px; padding: 10px; color: #666;\">" +
            "<p>Thank you for choosing <strong>Mars Arena</strong>!</p>" +
            "<p>Regards,<br><strong>Mars Arena Team</strong></p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>";

        sendHtmlEmail(toEmail, subject, content);
        System.out.println("📧 [EMAIL SERVICE] Completed sendBookingConfirmationEmail for: " + toEmail);
    }

    /**
     * Shared method to send HTML emails
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        System.out.println("📧 [EMAIL SERVICE] Starting sendHtmlEmail to: " + to);
        System.out.println("📧 [EMAIL SERVICE] Subject: " + subject);
        
        try {
            System.out.println("📧 [EMAIL SERVICE] Creating MimeMessage...");
            MimeMessage message = mailSender.createMimeMessage();
            
            System.out.println("📧 [EMAIL SERVICE] Creating MimeMessageHelper...");
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            System.out.println("📧 [EMAIL SERVICE] Setting email properties...");
            helper.setFrom(SENDER_EMAIL, SENDER_NAME);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            System.out.println("📧 [EMAIL SERVICE] Attempting to send email...");
            mailSender.send(message);
            System.out.println("✅ [EMAIL SERVICE] Email sent successfully to: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ [EMAIL SERVICE] MessagingException: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email to " + to + ": " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ [EMAIL SERVICE] Unexpected error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unexpected error while sending email to " + to + ": " + e.getMessage(), e);
        }
        
        System.out.println("📧 [EMAIL SERVICE] Completed sendHtmlEmail to: " + to);
    }

    /**
     * Simple text email for testing
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        System.out.println("📧 [EMAIL SERVICE] Sending simple email to: " + to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("✅ [EMAIL SERVICE] Simple email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ [EMAIL SERVICE] Simple email failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send simple email: " + e.getMessage(), e);
        }
    }

    /**
     * Backup plain method to send email
     */
    public void sendEmail(String to, String subject, String content) {
        System.out.println("📧 [EMAIL SERVICE] Starting sendEmail (plain) to: " + to);
        sendHtmlEmail(to, subject, content);
    }
}