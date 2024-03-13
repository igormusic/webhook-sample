package com.tvmsoftware.samplewebhook;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static org.junit.jupiter.api.Assertions.*;

class WebhookControllerTest {

    @Test
    void when_correct_signature_success() {
        var controller = new WebhookController("your_webhook_secret");

        var result = controller.handleWebhook("sha1=39786973e7e79ce163d7273db9e10c0230748f31", """
                {
                    "event_id":1209283434,
                    "event_type": "NEW_TRANSACTION"
                }""");

        assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
    }

    @Test
    void when_incorrect_signature_fail() {
        var controller = new WebhookController("your_webhook_secret");

        var result = controller.handleWebhook("sha1=39786973e7e79ce163d7273db9e10c0230748f31", """
                {
                    "event_id":343434343,
                    "event_type":"OLD_TRANSACTION"
                }""");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }


    @Test
    void when_no_signature_fail() {
        var controller = new WebhookController("your_webhook_secret");

        var result = controller.handleWebhook(null, """
                {
                    "event_id":343434343,
                    "event_type":"OLD_TRANSACTION"
                }""");

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }
}