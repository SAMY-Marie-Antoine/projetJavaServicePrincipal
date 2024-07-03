package fr.formation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Compte;
import jakarta.validation.Valid;

public interface CompteRepository extends JpaRepository<Compte, String> {

    List<Compte> findByUtilisateurId(String userId);

    //trouver un compte avec son nom
    List<Compte> findByNom(@Valid String nom);
      
}
