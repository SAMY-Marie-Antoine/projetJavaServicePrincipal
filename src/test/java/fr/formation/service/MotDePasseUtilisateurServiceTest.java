package fr.formation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MotDePasseUtilisateurServiceTest {
    
    @Mock
    private EmailService emailService;

    @InjectMocks
    private MotDePasseUtilisateurService motDePasseUtilisateurService;

    @Captor
    private ArgumentCaptor<String> toCaptor;
    
    @Captor
    private ArgumentCaptor<String> subjectCaptor;
    
    @Captor
    private ArgumentCaptor<String> textCaptor;

    private String email;
    private String resetToken;

    @BeforeEach
    public void setUp() {
        email = "test@example.com";
        resetToken = "sample-token";
    }

    @Test
    public void testSendPasswordResetLink() {
        // given
        String expectedSubject = "Réinitialisation du mot de passe";
        String expectedText = "Pour réinitialiser votre mot de passe, veuillez cliquer sur le lien suivant : http://localhost:4200/reset-password?token=" + resetToken;
        
        // when
        motDePasseUtilisateurService.sendPasswordResetLink(email, resetToken);

        // then
        verify(emailService).sendEmail(
                toCaptor.capture(),
                subjectCaptor.capture(),
                textCaptor.capture()
        );

        assertEquals(email, toCaptor.getValue());
        assertEquals(expectedSubject, subjectCaptor.getValue());
        assertEquals(expectedText, textCaptor.getValue());
    }
}
