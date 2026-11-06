package com.gossamercms.firebase.services;

/**
 * This service is responsible for sending SMS notifications using the Twilio API.
 * It initializes the Twilio environment once on application startup and provides a method to send SMS messages.
 * The service uses configuration properties for Twilio credentials and the sender's phone number.
 *
 * This is located in the Firebase module, but it is not directly related to Firebase push notifications.
 * Firebase uses Twilio for sending SMS messages, which is why this service is included here.
 */

import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.notifications.services.SmsDeliveryService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;


import jakarta.annotation.PostConstruct;

@ModuleService
public class FirebaseSmsNotificationService implements SmsDeliveryService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        // Initialize Twilio environment once on application startup
        Twilio.init(accountSid, authToken);
    }

    //TODO: Consider adding error handling and logging for failed SMS deliveries.
    // During development the twilio account may not be able to send messages to unverified numbers, so this should be handled gracefully.
    // During development, the Twilio account only sends a provided template, not the actual message body.
    // This is a limitation of Twilio's trial accounts.
    @Override
    public void send(String recipientPhoneNumber, String messageBody) {
        // Enforce E.164 phone format (e.g., "+16045551234")
        Message message = Message.creator(
                new PhoneNumber(recipientPhoneNumber),
                new PhoneNumber(fromNumber),
                "sms_appointment_reminders"//messageBody
        ).create();

      //  return message.getSid(); // Returns unique message tracking ID
    }


}