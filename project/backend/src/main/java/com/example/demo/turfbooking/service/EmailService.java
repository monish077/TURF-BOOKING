package com.example.demo.turfbooking.service;

import com.example.demo.turfbooking.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ── URLs ───────────────────────────────────────────────────────────────
    private static final String FRONTEND_URL  = "https://turf-booking-seven.vercel.app";
    private static final String SENDER_EMAIL  = "monidhoni0007@gmail.com";
    private static final String SENDER_NAME   = "Mars Arena Turf Booking";

    // ══════════════════════════════════════════════════════════════════════
    //  PUBLIC METHODS
    // ══════════════════════════════════════════════════════════════════════

    /** Verification email sent on new registration. */
    public void sendVerificationEmail(User user) {
        String verifyURL = FRONTEND_URL + "/verify-email?token=" + user.getVerificationToken();

        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#2e7d32;">✅ Verify Your Email — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>Thanks for registering! Click the button below to verify your email address:</p>
              <div style="text-align:center;margin:30px 0;">
                <a href="%s"
                   style="background:#2e7d32;color:#fff;padding:14px 28px;text-decoration:none;border-radius:6px;font-size:16px;font-weight:bold;">
                  Verify My Email
                </a>
              </div>
              <p style="color:#888;font-size:13px;">If the button doesn't work, paste this link into your browser:<br>%s</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(user.getName(), verifyURL, verifyURL);

        sendHtmlEmail(user.getEmail(), "✅ Verify your email — Mars Arena Turf Booking", html);
    }

    /** Password reset email. */
    public void sendResetPasswordEmail(User user) {
        String resetURL = FRONTEND_URL + "/reset-password?token=" + user.getResetPasswordToken();

        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#c62828;">🔐 Reset Your Password — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>We received a request to reset your password. Click the button below:</p>
              <div style="text-align:center;margin:30px 0;">
                <a href="%s"
                   style="background:#c62828;color:#fff;padding:14px 28px;text-decoration:none;border-radius:6px;font-size:16px;font-weight:bold;">
                  Reset Password
                </a>
              </div>
              <p style="color:#888;font-size:13px;">Link: %s</p>
              <p style="color:#888;font-size:13px;">If you didn't request a reset, you can safely ignore this email.</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(user.getName(), resetURL, resetURL);

        sendHtmlEmail(user.getEmail(), "🔐 Reset your password — Mars Arena", html);
    }

    /** Booking confirmation email after successful payment. */
    public void sendBookingConfirmationEmail(
            String toEmail,
            String userName,
            String turfName,
            String date,
            String slot,
            String price
    ) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:0 auto;padding:20px;background:#f9f9f9;border-radius:8px;">
              <h2 style="color:#1565c0;">🎉 Booking Confirmed — Mars Arena</h2>
              <p>Hello <strong>%s</strong>,</p>
              <p>Your turf booking is confirmed! Here are your details:</p>
              <table style="width:100%%;border-collapse:collapse;margin:16px 0;">
                <tr style="background:#e3f2fd;">
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Turf</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Date</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr style="background:#e3f2fd;">
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Slot</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">%s</td>
                </tr>
                <tr>
                  <td style="padding:10px;border:1px solid #ddd;"><strong>Price</strong></td>
                  <td style="padding:10px;border:1px solid #ddd;">₹%s</td>
                </tr>
              </table>
              <p>📧 No further action needed. See you on the turf!</p>
              <hr style="border:none;border-top:1px solid #ddd;margin:20px 0;">
              <p style="color:#555;">Regards,<br><strong>Mars Arena Team</strong></p>
            </div>
            """.formatted(userName, turfName, date, slot, price);

        sendHtmlEmail(toEmail, "🎉 Booking Confirmed — " + turfName, html);
    }

    /** Generic plain/html email for diagnostics or custom use. */
    public void sendEmail(String to, String subject, String content) {
        sendHtmlEmail(to, subject, content);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRIVATE CORE SENDER
    // ══════════════════════════════════════════════════════════════════════

    /**
     * Core HTML mail sender.
     * Uses InternetAddress directly to avoid UnsupportedEncodingException
     * from the MimeMessageHelper.setFrom(String, String) overload.
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        System.out.println("[EMAIL] Attempting to send email to: " + to);
        System.out.println("[EMAIL] Subject: " + subject);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // ✅ Use InternetAddress to avoid UnsupportedEncodingException
            try {
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            } catch (UnsupportedEncodingException e) {
                System.err.println("[EMAIL] setFrom encoding error, falling back to plain address: " + e.getMessage());
                message.setFrom(new InternetAddress(SENDER_EMAIL));
            }

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("[EMAIL] ✅ Sent successfully to: " + to);

        } catch (MessagingException e) {
            // Dig into the cause chain for the real reason
            Throwable root = getRootCause(e);
            System.err.println("[EMAIL] ❌ MessagingException sending to " + to);
            System.err.println("[EMAIL]    Message : " + e.getMessage());
            System.err.println("[EMAIL]    Root cause: " + root.getClass().getName() + " — " + root.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email send failed (SMTP): " + root.getMessage(), e);

        } catch (Exception e) {
            Throwable root = getRootCause(e);
            System.err.println("[EMAIL] ❌ Unexpected error sending to " + to);
            System.err.println("[EMAIL]    Type: " + e.getClass().getName());
            System.err.println("[EMAIL]    Message: " + e.getMessage());
            System.err.println("[EMAIL]    Root cause: " + root.getClass().getName() + " — " + root.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email send failed: " + root.getMessage(), e);
        }
    }

    /** Walks the exception cause chain to find the deepest cause. */
    private Throwable getRootCause(Throwable t) {
        Throwable cause = t.getCause();
        return (cause == null) ? t : getRootCause(cause);
    }
}
