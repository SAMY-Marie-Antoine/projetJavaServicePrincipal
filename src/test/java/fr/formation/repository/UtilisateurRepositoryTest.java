package fr.formation.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import fr.formation.model.Utilisateur;

@DataJpaTest
@Sql(scripts = "/utilisateurs.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear-all.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class UtilisateurRepositoryTest {

    @Autowired
    private UtilisateurRepository repository;

    /**
     * Test pour vérifier que la méthode findAll trouve bien tous les utilisateurs.
     * Ce test vérifie que le repository retourne deux utilisateurs insérés par le script SQL.
     */
    @Test
    public void shouldFindAllFindsTwo() {
        // given

        // when
        List<Utilisateur> utilisateurs = this.repository.findAll();

        // then
        Assertions.assertEquals(2, utilisateurs.size());
    }

    /**
     * Test pour vérifier que la méthode findById retourne un utilisateur existant.
     * Ce test vérifie que le repository retourne le bon utilisateur en fonction de son ID.
     */
    @Test
    public void shouldFindByIdReturnUtilisateur() {
        // given
        String existingId = "1"; // Assurez-vous que cet ID existe dans le script SQL

        // when
        Optional<Utilisateur> utilisateur = this.repository.findById(existingId);

        // then
        Assertions.assertTrue(utilisateur.isPresent());
        Assertions.assertEquals("Utilisateur 1", utilisateur.get().getNom());
    }

    /**
     * Test pour vérifier que la méthode save ajoute un nouvel utilisateur.
     * Ce test vérifie que le repository enregistre correctement un nouvel utilisateur et lui assigne un ID.
     */
    @Test
    public void shouldSaveAddNewUtilisateur() {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Nouvel Utilisateur");
        utilisateur.setDateDeNaissance(LocalDate.of(1995, 5, 15));
        utilisateur.setEmail("nouvelutilisateur@example.com");
        utilisateur.setMotDePasse("password123");

        // when
        Utilisateur savedUtilisateur = this.repository.save(utilisateur);

        // then
        Assertions.assertNotNull(savedUtilisateur.getId());
        Assertions.assertEquals("Nouvel Utilisateur", savedUtilisateur.getNom());
    }

    /**
     * Test pour vérifier que la méthode delete supprime un utilisateur.
     * Ce test vérifie que le repository supprime correctement un utilisateur en fonction de son ID.
     */
    @Test
    public void shouldDeleteRemoveUtilisateur() {
        // given
        String existingId = "2"; // Assurez-vous que cet ID existe dans le script SQL

        // when
        this.repository.deleteById(existingId);
        Optional<Utilisateur> utilisateur = this.repository.findById(existingId);

        // then
        Assertions.assertFalse(utilisateur.isPresent());
    }
}
