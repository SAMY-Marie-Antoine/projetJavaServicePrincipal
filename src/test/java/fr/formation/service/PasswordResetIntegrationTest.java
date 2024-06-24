package fr.formation.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import fr.formation.model.Utilisateur;
import fr.formation.repository.UtilisateurRepository;



@SpringBootTest
@AutoConfigureMockMvc
public class PasswordResetIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private MotDePasseUtilisateurService motDePasseUtilisateurService;

    @BeforeEach
    public void setup() {
        utilisateurRepository.deleteAll();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");
        utilisateurRepository.save(utilisateur);
        System.out.println("Utilisateur créé : " + utilisateur.getEmail());
    }

    @Test
    public void testPasswordResetFlow() throws Exception {
        // given
        String email = "test@example.com";

        // when
        System.out.println("Envoi de la demande de réinitialisation du mot de passe...");
        mockMvc.perform(post("/forgot-password")
                .contentType("application/json")
                .content("{\"email\":\"" + email + "\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Un lien de réinitialisation du mot de passe a été envoyé à votre email"));

        // then
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElseThrow();
        String resetToken = utilisateur.getResetToken();
        System.out.println("Token de réinitialisation : " + resetToken);
        mockMvc.perform(post("/reset-password")
                .contentType("application/json")
                .content("{\"token\":\"" + resetToken + "\", \"newPassword\":\"new-password\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        
        System.out.println("Mot de passe réinitialisé.");

        // Vérifiez que le mot de passe a été mis à jour       
        utilisateur = utilisateurRepository.findByEmail(email).orElseThrow();
        String newPassword = "new-password";

        assertTrue(motDePasseUtilisateurService.crypterMotDePasse(newPassword).equals(utilisateur.getMotDePasse()));
        System.out.println("Le mot de passe a été mis à jour.");
    }
}
