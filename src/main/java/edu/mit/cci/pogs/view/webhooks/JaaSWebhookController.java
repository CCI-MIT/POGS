package edu.mit.cci.pogs.view.webhooks;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class JaaSWebhookController {

    @PostMapping("/jitsiwebhook")
    public void process(@RequestBody com.fasterxml.jackson.databind.JsonNode payload) {
        JsonNode eventType = payload.get("eventType");
        JsonNode data = payload.get("data");
        JsonNode preAuthenticatedLink = data.get("preAuthenticatedLink");

        JsonNode fqn = payload.get("fqn");
        //eventType == TRANSCRIPTION_UPLOADED RECORDING_UPLOADED
        //1. Get session from fqn
        //2. Save JSON to session
        //3. Send email to session's config with sessionURL
        System.out.println(payload);
    }
}
