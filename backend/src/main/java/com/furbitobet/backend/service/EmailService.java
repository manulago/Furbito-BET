package com.furbitobet.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Async
    public void sendWelcomeEmail(String to, String username, String password) {
        System.out.println("Attempting to send welcome email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("furbitobet@gmail.com");
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

        try {
            emailSender.send(message);
            System.out.println("Welcome email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to " + to + ": " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Async sending simple email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("furbitobet@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            emailSender.send(message);
            System.out.println("Simple email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send simple email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
