package com.tvmsoftware.samplewebhook;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class WebhookController {

// get secret from config file

    private final String secretValue;

    public WebhookController(@Value("${secret}") String secretValue) {
        this.secretValue = secretValue;
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestHeader("Signature") String signature,
                                                @RequestBody String payload) {

        if (isValidSignature(signature, payload)) {
            // Process the payload as needed
            log.info("Valid signature. Payload: " + payload);
            return ResponseEntity.ok().body("Webhook received successfully.");
        } else {
            log.error("Invalid signature. Payload: " + payload + " signature provided: " + signature);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature.");
        }

    }

    private boolean isValidSignature(String signature, String payload) {
        String computedSignature = "sha1=" + calculateHMAC(payload, secretValue);
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
            log.error("failed to calculate HMAC", e);
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