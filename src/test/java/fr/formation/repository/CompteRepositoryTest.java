package fr.formation.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import fr.formation.model.Compte;

@DataJpaTest
@Sql(scripts = {"/utilisateurs.sql", "/comptes.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear-all.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class CompteRepositoryTest {
    
    @Autowired
    private CompteRepository repository;

    @Test
    public void shouldFindAllFindsTwo() {
        // when
        List<Compte> comptes = this.repository.findAll();

        // then
        Assertions.assertEquals(2, comptes.size());
    }

    @Test
    public void shouldFindByIdReturnCompte() {
        // given
        String existingId = "1"; // Assurez-vous que cet ID existe dans le script SQL

        // when
        Optional<Compte> compte = this.repository.findById(existingId);

        // then
        Assertions.assertTrue(compte.isPresent());
        Assertions.assertEquals("Compte1", compte.get().getNom());
    }

    @Test
    public void shouldSaveAddNewCompte() {
        // given
        Compte compte = new Compte();
        compte.setNom("Nouveau Compte");
        // Ajoutez ici les autres propriétés de Compte

        // when
        Compte savedCompte = this.repository.save(compte);

        // then
        Assertions.assertNotNull(savedCompte.getId());
        Assertions.assertEquals("Nouveau Compte", savedCompte.getNom());
    }

    @Test
    public void shouldDeleteRemoveCompte() {
        // given
        String existingId = "2"; // Assurez-vous que cet ID existe dans le script SQL

        // when
        this.repository.deleteById(existingId);
        Optional<Compte> compte = this.repository.findById(existingId);

        // then
        Assertions.assertFalse(compte.isPresent());
    }

    @Test
    public void shouldFindByUtilisateurIdReturnComptes() {
        // given
        String existingUserId = "1"; // Assurez-vous que cet ID utilisateur existe dans le script SQL

        // when
        List<Compte> comptes = this.repository.findByUtilisateurId(existingUserId);

        // then
        Assertions.assertNotNull(comptes);
        Assertions.assertFalse(comptes.isEmpty()); // Assurez-vous qu'il y a au moins un compte pour cet utilisateur dans le script SQL
    }

    @Test
    public void shouldFindByNomReturnComptes() {
        // given
        String existingNom = "Compte1"; // Assurez-vous que ce nom de compte existe dans le script SQL

        // when
        List<Compte> comptes = this.repository.findByNom(existingNom);

        // then
        Assertions.assertNotNull(comptes);
        Assertions.assertFalse(comptes.isEmpty()); // Assurez-vous qu'il y a au moins un compte avec ce nom dans le script SQL
    }

}
