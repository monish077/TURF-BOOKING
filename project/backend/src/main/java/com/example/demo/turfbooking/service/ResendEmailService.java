package com.example.demo.turfbooking.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendVerificationEmail(String toEmail, String verificationUrl) {
        try {
            Resend resend = new Resend(resendApiKey);

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; }
                        .header { background: #4CAF50; color: white; padding: 10px; text-align: center; border-radius: 5px; }
                        .button { background: #4CAF50; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; display: inline-block; }
                        .footer { margin-top: 20px; font-size: 12px; color: #666; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>🎉 Welcome to Turf Booking!</h2>
                        </div>
                        <p>Hello,</p>
                        <p>Thank you for registering with Turf Booking. Please verify your email address by clicking the button below:</p>
                        <p style="text-align: center;">
                            <a href="%s" class="button">Verify Email Address</a>
                        </p>
                        <p>Or copy and paste this link in your browser:</p>
                        <p><code>%s</code></p>
                        <div class="footer">
                            <p>If you didn't create an account, please ignore this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);

            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .from("Turf Booking <onboarding@resend.dev>")
                    .to(toEmail)
                    .subject("Verify Your Email - Turf Booking")
                    .html(htmlContent)
                    .build();

            SendEmailResponse response = resend.emails().send(sendEmailRequest);
            System.out.println("✅ Resend email sent successfully! Email ID: " + response.getId());

        } catch (ResendException e) {
            System.out.println("❌ Resend email failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    public void sendTestEmail(String toEmail) {
        try {
            Resend resend = new Resend(resendApiKey);

            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .from("Turf Booking <onboarding@resend.dev>")
                    .to(toEmail)
                    .subject("✅ Test Email from Turf Booking")
                    .html("<p>This is a <strong>test email</strong> from your Turf Booking application using Resend!</p>")
                    .build();

            SendEmailResponse response = resend.emails().send(sendEmailRequest);
            System.out.println("✅ Resend test email sent! ID: " + response.getId());

        } catch (ResendException e) {
            System.out.println("❌ Resend test email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}