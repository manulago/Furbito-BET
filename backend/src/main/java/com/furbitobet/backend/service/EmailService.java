package com.furbitobet.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.url}")
    private String brevoApiUrl;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendWelcomeEmail(String to, String username, String password) {
        System.out.println("Attempting to send welcome email via Brevo to: " + to);
        String subject = "Bienvenido a FurbitoBET - Confirmación de Registro";
        String htmlContent = "<html><body>" +
                "<p>Hola " + username + ",</p>" +
                "<p>¡Gracias por registrarte en FurbitoBET!</p>" +
                "<p>Tu cuenta ha sido creada exitosamente.</p>" +
                "<p>Tus credenciales de acceso son:</p>" +
                "<p><strong>Usuario:</strong> " + username + "</p>" +
                "<p><strong>Contraseña:</strong> " + password + "</p>" +
                "<p>Por favor, guarda esta información en un lugar seguro.</p>" +
                "<p>¡Buena suerte con tus apuestas!</p>" +
                "<p>El equipo de FurbitoBET</p>" +
                "</body></html>";

        sendEmail(to, username, subject, htmlContent);
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Async sending simple email via Brevo to: " + to);
        // Convert text to simple HTML
        String htmlContent = "<html><body><p>" + text.replace("\n", "<br>") + "</p></body></html>";
        sendEmail(to, to, subject, htmlContent);
    }

    private void sendEmail(String toEmail, String toName, String subject, String htmlContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> body = new HashMap<>();
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "FurbitoBET");
            sender.put("email", "furbitobet@gmail.com");
            body.put("sender", sender);

            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", toEmail);
            recipient.put("name", toName);
            body.put("to", Collections.singletonList(recipient));

            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(brevoApiUrl, request, String.class);
            System.out.println("Email sent successfully via Brevo to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email via Brevo to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
