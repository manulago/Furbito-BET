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
    public void sendWelcomeEmail(String to, String username) {
        System.out.println("Attempting to send welcome email via Brevo to: " + to);
        String subject = "Bienvenido a FurbitoBET - Confirmación de Registro";

        // Styled HTML with CSS
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                +
                ".header { text-align: center; border-bottom: 2px solid #4CAF50; padding-bottom: 20px; margin-bottom: 20px; }"
                +
                ".header h1 { color: #333; margin: 0; font-size: 28px; }" +
                ".content { color: #555; font-size: 16px; line-height: 1.6; }" +
                ".footer { margin-top: 30px; text-align: center; font-size: 12px; color: #999; }" +
                ".button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>¡Bienvenido a FurbitoBET!</h1></div>" +
                "<div class='content'>" +
                "<p>Hola <strong>" + username + "</strong>,</p>" +
                "<p>¡Gracias por registrarte en la mejor plataforma de apuestas deportivas!</p>" +
                "<p>Tu cuenta ha sido creada exitosamente y ya estás listo para empezar a jugar.</p>" +
                "<p style='text-align: center;'><a href='https://furbitobet.com' class='button'>Ir a FurbitoBET</a></p>"
                +
                "</div>" +
                "<div class='footer'>" +
                "<p>Si no has creado esta cuenta, por favor ignora este correo.</p>" +
                "<p>&copy; 2025 FurbitoBET. Todos los derechos reservados.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, username, subject, htmlContent);
    }

    @Async
    public void sendResetPasswordEmail(String to, String token) {
        System.out.println("Attempting to send reset password email via Brevo to: " + to);
        String subject = "FurbitoBET - Restablecer Contraseña";

        // This link should point to the frontend route we will create
        String resetLink = "http://localhost:5173/reset-password?token=" + token; // Ideally use environment variable
                                                                                  // for frontend URL

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                +
                ".header { text-align: center; border-bottom: 2px solid #FF5722; padding-bottom: 20px; margin-bottom: 20px; }"
                +
                ".header h1 { color: #333; margin: 0; font-size: 28px; }" +
                ".content { color: #555; font-size: 16px; line-height: 1.6; }" +
                ".footer { margin-top: 30px; text-align: center; font-size: 12px; color: #999; }" +
                ".button { display: inline-block; padding: 12px 24px; background-color: #FF5722; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Restablecer Contraseña</h1></div>" +
                "<div class='content'>" +
                "<p>Hola,</p>" +
                "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>" +
                "<p>Haz clic en el siguiente enlace para crear una nueva contraseña:</p>" +
                "<p style='text-align: center;'><a href='" + resetLink
                + "' class='button'>Restablecer Contraseña</a></p>" +
                "<p>Si no has solicitado esto, puedes ignorar este correo de forma segura.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2025 FurbitoBET. Todos los derechos reservados.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, to, subject, htmlContent);
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
