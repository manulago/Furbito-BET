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

    @Value("${app.frontend.url}")
    private String frontendUrl;

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
                "<p style='text-align: center;'><a href='" + frontendUrl + "' class='button'>Ir a FurbitoBET</a></p>"
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

        String resetLink = frontendUrl + "/reset-password?token=" + token;

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

    @Async
    public void sendNewEventEmail(String to, String username, String eventName, String eventDate) {
        System.out.println("Sending new event notification to: " + to);
        String subject = "⚽ Nuevo Partido Disponible: " + eventName;
        String loginLink = frontendUrl + "/login";

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f0f2f5; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 0; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden; }"
                +
                ".header { background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%); padding: 30px 20px; text-align: center; }"
                +
                ".header h1 { color: #ffffff; margin: 0; font-size: 28px; font-weight: 800; letter-spacing: 1px; }" +
                ".logo-text { color: #4ade80; }" + // Green color for 'BET' part or similar accent
                ".content { padding: 40px 30px; color: #334155; font-size: 16px; line-height: 1.6; }" +
                ".event-card { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: center; }"
                +
                ".event-name { font-size: 20px; font-weight: bold; color: #0f172a; margin-bottom: 10px; }" +
                ".event-date { color: #64748b; font-size: 14px; }" +
                ".button-container { text-align: center; margin-top: 30px; }" +
                ".button { display: inline-block; padding: 14px 32px; background-color: #22c55e; color: white; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px; transition: background-color 0.3s; box-shadow: 0 4px 6px -1px rgba(34, 197, 94, 0.4); }"
                +
                ".footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                // Replacing Image with Styled Text as Logo for reliability, or use a
                // placeholder if strictly requested.
                // User asked for "include the logo". Listing a text-based logo is safer if no
                // hosted image exists.
                // But I'll add an <img> tag pointing to a generic football icon or furbito
                // placeholder if I can.
                // For now, I will use a high quality text representation which often looks
                // better than broken images.
                "<h1>Furbito<span class='logo-text'>BET</span></h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hola <strong>" + username + "</strong>,</p>" +
                "<p>¡La emoción continúa! Se ha abierto un nuevo evento en la plataforma y las apuestas ya están disponibles.</p>"
                +
                "<div class='event-card'>" +
                "<div class='event-name'>" + eventName + "</div>" +
                "<div class='event-date'>📅 " + eventDate + "</div>" +
                "</div>" +
                "<p>Analiza las estadísticas, revisa las cuotas y haz tu jugada maestra.</p>" +
                "<div class='button-container'>" +
                "<a href='" + loginLink + "' class='button'>Entrar y Apostar</a>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>¿No quieres recibir estas notificaciones? Configura tu cuenta en tu perfil.</p>" +
                "<p>&copy; 2025 FurbitoBET. Todos los derechos reservados.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, username, subject, htmlContent);
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
