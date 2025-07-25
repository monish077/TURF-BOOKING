package com.example.demo.turfbooking.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    // Twilio credentials
   @Value("${twilio.accountSid}")
private String accountSid;

@Value("${twilio.authToken}")
private String authToken;



    // Twilio Sandbox WhatsApp Number
    private static final String FROM_NUMBER = "whatsapp:+14155238886";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendBookingConfirmation(String toWhatsAppNumber, String userName, String turfName, String date, String slot) {
        String messageBody = String.format(
            "✅ Hello %s!\n\nYour booking for *%s* on *%s* at *%s* is confirmed.\n\nThank you!\n- Mars Arena ⚽🌱",
            userName, turfName, date, slot
        );

        Message message = Message.creator(
            new PhoneNumber("whatsapp:" + toWhatsAppNumber),
            new PhoneNumber(fromNumber),
            messageBody
        ).create();

        System.out.println("✅ WhatsApp message sent, SID: " + message.getSid());
    }
}
