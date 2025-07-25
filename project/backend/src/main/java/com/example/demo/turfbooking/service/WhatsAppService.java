package com.example.demo.turfbooking.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    // Twilio credentials from application.properties
    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.fromNumber}")
    private String fromNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
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
