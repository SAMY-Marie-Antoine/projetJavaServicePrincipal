package fr.formation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        // given
        String to = "destinataire@example.com";
        String subject = "Sujet de Test";
        String text = "Contenu de l'email de test";

        // when
        emailService.sendEmail(to, subject, text);

        // then
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
    
}
