package com.furbitobet.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendWelcomeEmail(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@furbitobet.com");
        message.setTo(to);
        message.setSubject("Bienvenido a FurbitoBET - Confirmación de Registro");
        message.setText("Hola " + username + ",\n\n" +
                "¡Gracias por registrarte en FurbitoBET!\n\n" +
                "Tu cuenta ha sido creada exitosamente.\n" +
                "Tus credenciales de acceso son:\n\n" +
                "Usuario: " + username + "\n" +
                "Contraseña: " + password + "\n\n" +
                "Por favor, guarda esta información en un lugar seguro.\n\n" +
                "¡Buena suerte con tus apuestas!\n" +
                "El equipo de FurbitoBET");

        emailSender.send(message);
    }
}
