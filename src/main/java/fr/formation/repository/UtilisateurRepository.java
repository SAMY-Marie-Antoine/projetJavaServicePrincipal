package fr.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {
    
}
