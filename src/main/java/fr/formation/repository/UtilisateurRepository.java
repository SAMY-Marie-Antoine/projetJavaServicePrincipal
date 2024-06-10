package fr.formation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {
	
	Optional<Utilisateur> findByEmailAndMotDePasse(String email, String motDePasse);
	Optional<Utilisateur> findByEmail(String email);
    
}
