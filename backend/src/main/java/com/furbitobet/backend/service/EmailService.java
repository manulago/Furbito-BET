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
    public void sendProfileUpdateConfirmation(String to, String token) {
        System.out.println("Sending profile update confirmation to: " + to);
        String subject = "FurbitoBET - Confirmar Cambios de Perfil";
        String confirmLink = frontendUrl + "/profile/confirm?token=" + token;

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                +
                ".header { text-align: center; border-bottom: 2px solid #2196F3; padding-bottom: 20px; margin-bottom: 20px; }"
                +
                ".header h1 { color: #333; margin: 0; font-size: 28px; }" +
                ".content { color: #555; font-size: 16px; line-height: 1.6; }" +
                ".button { display: inline-block; padding: 12px 24px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>Confirmar Cambios</h1></div>" +
                "<div class='content'>" +
                "<p>Hola,</p>" +
                "<p>Has solicitado actualizar tu perfil (nombre de usuario, email o contraseña).</p>" +
                "<p>Para confirmar y aplicar estos cambios, por favor haz clic en el siguiente enlace:</p>" +
                "<p style='text-align: center;'><a href='" + confirmLink + "' class='button'>Confirmar Cambios</a></p>"
                +
                "<p>El enlace expirará en 24 horas.</p>" +
                "<p>Si no has realizado esta solicitud, ignora este correo.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, to, subject, htmlContent);
    }

    @Async
    public void sendAccountConfirmationEmail(String to, String token) {
        System.out.println("Sending account confirmation to: " + to);
        String subject = "FurbitoBET - Activa tu Cuenta";
        String confirmLink = frontendUrl + "/confirm-account?token=" + token;

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
                ".button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin-top: 20px; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'><h1>¡Activa tu Cuenta!</h1></div>" +
                "<div class='content'>" +
                "<p>Hola,</p>" +
                "<p>Gracias por registrarte en FurbitoBET.</p>" +
                "<p>Para activar tu cuenta y empezar a apostar, por favor haz clic en el siguiente enlace:</p>" +
                "<p style='text-align: center;'><a href='" + confirmLink + "' class='button'>Activar Cuenta</a></p>"
                +
                "<p>El enlace expirará en 24 horas.</p>" +
                "<p>Si no has creado esta cuenta, por favor ignora este correo.</p>" +
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
    public void sendNewsEmail(String to, String username) {
        System.out.println("Sending news email to: " + to);
        String subject = "🎉 ¡Novedades en FurbitoBET!";
        String appLink = frontendUrl;

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0f172a; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #1e293b; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }"
                +
                ".header { background: linear-gradient(135deg, #22c55e 0%, #3b82f6 100%); padding: 40px 30px; text-align: center; }"
                +
                ".header h1 { color: #ffffff; margin: 0; font-size: 32px; font-weight: 800; text-shadow: 0 2px 4px rgba(0,0,0,0.2); }"
                +
                ".header p { color: #e0f2fe; margin: 10px 0 0 0; font-size: 16px; }" +
                ".content { padding: 40px 30px; }" +
                ".greeting { color: #f1f5f9; font-size: 18px; margin-bottom: 20px; }" +
                ".intro { color: #cbd5e1; font-size: 16px; line-height: 1.6; margin-bottom: 30px; }" +
                ".features { margin: 30px 0; }" +
                ".feature-card { background: linear-gradient(135deg, #334155 0%, #1e293b 100%); border-left: 4px solid #22c55e; border-radius: 12px; padding: 20px; margin-bottom: 20px; transition: transform 0.3s; }"
                +
                ".feature-icon { font-size: 32px; margin-bottom: 10px; }" +
                ".feature-title { color: #22c55e; font-size: 18px; font-weight: bold; margin-bottom: 8px; }" +
                ".feature-desc { color: #cbd5e1; font-size: 14px; line-height: 1.5; }" +
                ".highlight { background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%); border-left-color: #3b82f6; }"
                +
                ".highlight .feature-title { color: #60a5fa; }" +
                ".button-container { text-align: center; margin: 40px 0; }" +
                ".button { display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%); color: white; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 18px; box-shadow: 0 8px 16px rgba(34, 197, 94, 0.4); transition: all 0.3s; }"
                +
                ".footer { background-color: #0f172a; padding: 30px; text-align: center; border-top: 1px solid #334155; }"
                +
                ".footer p { color: #64748b; font-size: 12px; margin: 5px 0; }" +
                ".divider { height: 2px; background: linear-gradient(90deg, transparent, #334155, transparent); margin: 30px 0; }"
                +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +

                "<!-- Header -->" +
                "<div class='header'>" +
                "<h1>🎉 ¡Novedades en FurbitoBET!</h1>" +
                "<p>Descubre todas las mejoras que hemos preparado para ti</p>" +
                "</div>" +

                "<!-- Content -->" +
                "<div class='content'>" +
                "<p class='greeting'>¡Hola <strong>" + (username != null ? username : "") + "</strong>!</p>" +
                "<p class='intro'>Tenemos grandes novedades en FurbitoBET que queremos compartir contigo. Hemos trabajado duro para mejorar tu experiencia y hacerla aún más emocionante.</p>"
                +

                "<!-- Features -->" +
                "<div class='features'>" +

                "<!-- Feature 1: PWA -->" +
                "<div class='feature-card highlight'>" +
                "<div class='feature-icon'>📱</div>" +
                "<div class='feature-title'>¡Instala la App!</div>" +
                "<div class='feature-desc'>" +
                "Ahora puedes instalar FurbitoBET en tu móvil o PC como una aplicación. " +
                "Acceso rápido desde tu pantalla de inicio, sin necesidad de abrir el navegador.<br><br>" +
                "<strong>🔹 En Android:</strong> Busca el botón \"Instalar App\" en la página<br>" +
                "<strong>🔹 En iPhone:</strong> Toca Compartir → \"Añadir a pantalla de inicio\"<br>" +
                "<strong>🔹 En PC:</strong> Busca el icono ⊕ en la barra de direcciones" +
                "</div>" +
                "</div>" +

                "<!-- Feature 2: Mobile -->" +
                "<div class='feature-card'>" +
                "<div class='feature-icon'>📱</div>" +
                "<div class='feature-title'>Mejora Móvil</div>" +
                "<div class='feature-desc'>" +
                "Experiencia 100% optimizada para tu teléfono. Navegación más fluida, accesible y rápida en cualquier dispositivo."
                +
                "</div>" +
                "</div>" +

                "<!-- Feature 3: Help -->" +
                "<div class='feature-card'>" +
                "<div class='feature-icon'>❓</div>" +
                "<div class='feature-title'>Nueva Página de Ayuda</div>" +
                "<div class='feature-desc'>" +
                "¿Dudas sobre cómo funciona algo? Visita nuestra nueva sección de ayuda con guías completas y tutoriales paso a paso."
                +
                "</div>" +
                "</div>" +

                "<!-- Feature 4: Profile -->" +
                "<div class='feature-card'>" +
                "<div class='feature-icon'>⚙️</div>" +
                "<div class='feature-title'>Gestión de Perfil</div>" +
                "<div class='feature-desc'>" +
                "Control total sobre tu cuenta. Actualiza tus datos, cambia tu contraseña y gestiona tus preferencias fácilmente."
                +
                "</div>" +
                "</div>" +

                "<!-- Feature 5: Public Profiles -->" +
                "<div class='feature-card'>" +
                "<div class='feature-icon'>👀</div>" +
                "<div class='feature-title'>Espía a los Mejores</div>" +
                "<div class='feature-desc'>" +
                "Visita el perfil de otros usuarios desde el ranking. Ve su historial de apuestas, estrategias y aprende de los mejores."
                +
                "</div>" +
                "</div>" +

                "</div>" +

                "<div class='divider'></div>" +

                "<!-- CTA Button -->" +
                "<div class='button-container'>" +
                "<a href='" + appLink + "' class='button'>🎰 Entrar a FurbitoBET</a>" +
                "</div>" +

                "<p class='intro' style='text-align: center; margin-top: 20px;'>" +
                "¡No esperes más! Entra ahora y descubre todas las mejoras que hemos preparado para ti." +
                "</p>" +

                "</div>" +

                "<!-- Footer -->" +
                "<div class='footer'>" +
                "<p>Este es un email informativo sobre las novedades de FurbitoBET</p>" +
                "<p>&copy; 2025 FurbitoBET. Todos los derechos reservados.</p>" +
                "<p style='margin-top: 15px;'>🎰 La mejor plataforma de apuestas deportivas</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, username != null ? username : to, subject, htmlContent);
    }

    @Async
    public void sendChristmasGiftEmail(String to, String username) {
        System.out.println("Sending Christmas gift email to: " + to);
        String subject = "🎄 ¡Regalo de Navidad: 100€ para la Gran Cena de Furbito! 🎁";
        String appLink = frontendUrl;

        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0f172a; margin: 0; padding: 0; }"
                +
                ".container { max-width: 600px; margin: 20px auto; background-color: #1e293b; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 30px rgba(0,0,0,0.3); }"
                +
                ".header { background: linear-gradient(135deg, #dc2626 0%, #16a34a 50%, #dc2626 100%); padding: 50px 30px; text-align: center; position: relative; }"
                +
                ".header::before { content: '❄️'; position: absolute; top: 10px; left: 20px; font-size: 30px; opacity: 0.7; }"
                +
                ".header::after { content: '🎅'; position: absolute; top: 10px; right: 20px; font-size: 30px; opacity: 0.7; }"
                +
                ".header h1 { color: #ffffff; margin: 0; font-size: 36px; font-weight: 800; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }"
                +
                ".header p { color: #fef3c7; margin: 10px 0 0 0; font-size: 18px; font-weight: 600; }" +
                ".content { padding: 40px 30px; }" +
                ".greeting { color: #f1f5f9; font-size: 18px; margin-bottom: 20px; text-align: center; }" +
                ".gift-box { background: linear-gradient(135deg, #16a34a 0%, #22c55e 100%); border-radius: 16px; padding: 30px; margin: 30px 0; text-align: center; box-shadow: 0 8px 20px rgba(34, 197, 94, 0.3); }"
                +
                ".gift-amount { font-size: 64px; font-weight: 900; color: #ffffff; margin: 10px 0; text-shadow: 0 4px 8px rgba(0,0,0,0.2); }"
                +
                ".gift-text { color: #dcfce7; font-size: 20px; font-weight: 600; margin-top: 10px; }" +
                ".message { color: #cbd5e1; font-size: 16px; line-height: 1.8; margin: 25px 0; text-align: center; }" +
                ".event-highlight { background: linear-gradient(135deg, #334155 0%, #1e293b 100%); border: 2px solid #22c55e; border-radius: 12px; padding: 25px; margin: 30px 0; text-align: center; }"
                +
                ".event-title { color: #22c55e; font-size: 24px; font-weight: bold; margin-bottom: 10px; }" +
                ".event-desc { color: #cbd5e1; font-size: 16px; line-height: 1.6; }" +
                ".button-container { text-align: center; margin: 40px 0; }" +
                ".button { display: inline-block; padding: 18px 45px; background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%); color: white; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 20px; box-shadow: 0 8px 16px rgba(220, 38, 38, 0.4); transition: all 0.3s; }"
                +
                ".footer { background-color: #0f172a; padding: 30px; text-align: center; border-top: 1px solid #334155; }"
                +
                ".footer p { color: #64748b; font-size: 12px; margin: 5px 0; }" +
                ".snowflakes { font-size: 24px; text-align: center; margin: 20px 0; letter-spacing: 10px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +

                "<!-- Header -->" +
                "<div class='header'>" +
                "<h1>🎄 ¡Feliz Navidad! 🎄</h1>" +
                "<p>Tenemos un regalo especial para ti</p>" +
                "</div>" +

                "<!-- Content -->" +
                "<div class='content'>" +
                "<p class='greeting'>¡Hola <strong>" + (username != null ? username : "") + "</strong>!</p>" +

                "<div class='snowflakes'>❄️ ⛄ 🎁 🔔 ⭐</div>" +

                "<p class='message'>" +
                "En FurbitoBET queremos celebrar estas fiestas contigo de una forma muy especial. " +
                "Por eso, hemos decidido regalarte algo que sabemos que te va a encantar..." +
                "</p>" +

                "<!-- Gift Box -->" +
                "<div class='gift-box'>" +
                "<div style='font-size: 48px; margin-bottom: 10px;'>🎁</div>" +
                "<div class='gift-amount'>100€</div>" +
                "<div class='gift-text'>¡GRATIS PARA APOSTAR!</div>" +
                "</div>" +

                "<p class='message'>" +
                "Así es, <strong>100€ completamente gratis</strong> que ya están disponibles en tu cuenta " +
                "para que los uses como quieras. Y qué mejor ocasión que la próxima..." +
                "</p>" +

                "<!-- Event Highlight -->" +
                "<div class='event-highlight'>" +
                "<div class='event-title'>⚽ Gran Cena de Furbito ⚽</div>" +
                "<div class='event-desc'>" +
                "El evento más esperado del año está a punto de llegar. " +
                "Usa tu regalo navideño para hacer tus apuestas y demostrar quién tiene el mejor olfato para predecir los resultados."
                +
                "<br><br>" +
                "<strong>¡Que gane el mejor!</strong> 🏆" +
                "</div>" +
                "</div>" +

                "<p class='message'>" +
                "No esperes más, entra ahora a FurbitoBET y empieza a planear tu estrategia ganadora. " +
                "Los 100€ ya están esperándote en tu cuenta." +
                "</p>" +

                "<!-- CTA Button -->" +
                "<div class='button-container'>" +
                "<a href='" + appLink + "' class='button'>🎰 Entrar y Apostar Ahora</a>" +
                "</div>" +

                "<div class='snowflakes'>🎅 🎄 ⛄ 🎁 ✨</div>" +

                "<p class='message' style='font-size: 14px; color: #94a3b8; margin-top: 30px;'>" +
                "Desde todo el equipo de FurbitoBET te deseamos unas felices fiestas y mucha suerte en tus apuestas." +
                "<br><strong>¡Feliz Navidad y próspero Año Nuevo! 🎊</strong>" +
                "</p>" +

                "</div>" +

                "<!-- Footer -->" +
                "<div class='footer'>" +
                "<p>Este es un regalo especial de Navidad de FurbitoBET</p>" +
                "<p>&copy; 2025 FurbitoBET. Todos los derechos reservados.</p>" +
                "<p style='margin-top: 15px;'>🎰 La mejor plataforma de apuestas deportivas</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";

        sendEmail(to, username != null ? username : to, subject, htmlContent);
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
