package com.tvmsoftware.samplewebhook;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class WebhookController {

// get secret from config file

    private final String secret = "your_webhook_secret";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestHeader("Signature") String signature,
                                                @RequestBody String payload) {

        if (isValidSignature(signature, payload)) {
            // Process the payload as needed
            System.out.println("Valid signature. Payload: " + payload);
            return ResponseEntity.ok().body("Webhook received successfully.");
        } else {
            System.err.println("Invalid signature. Payload: " + payload);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature.");
        }

    }

    private boolean isValidSignature(String signature, String payload) {
        String computedSignature = "sha1=" + calculateHMAC(payload, secret);
        return computedSignature.equals(signature);
    }

    private String calculateHMAC(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(data.getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}