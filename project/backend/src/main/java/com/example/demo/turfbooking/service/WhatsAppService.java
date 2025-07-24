package com.example.demo.turfbooking.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
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

    public void sendBookingConfirmation(String toWhatsAppNumber, String message) {
        Message messageObj = Message.creator(
            new PhoneNumber("whatsapp:" + toWhatsAppNumber),
            new PhoneNumber(FROM_NUMBER),
            message
        ).create();

        System.out.println("✅ WhatsApp message sent, SID: " + messageObj.getSid());
    }
}
