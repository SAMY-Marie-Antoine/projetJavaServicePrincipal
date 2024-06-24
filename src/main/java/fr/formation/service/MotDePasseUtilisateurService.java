package fr.formation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MotDePasseUtilisateurService {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    public MotDePasseUtilisateurService(EmailService emailService) {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.emailService = emailService;
    }

    public String crypterMotDePasse(String motDePasse) {
        return bCryptPasswordEncoder.encode(motDePasse);
    }

    public void sendPasswordResetLink(String email, String resetToken) {
        // 1. Créer l'URL de réinitialisation du mot de passe
        String resetPasswordUrl = "http://localhost:4200/reset-password?token=" + resetToken;
    
        // 2. Créer le contenu de l'email
        String emailContent = "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant : " + resetPasswordUrl;
    
        // 3. Envoyer l'email
        // On utilise un service d'envoi d'email pour cela, par exemple JavaMailSender comme on utilise Spring Boot
        emailService.sendEmail(email, "Réinitialisation du mot de passe", emailContent);
    }

}
