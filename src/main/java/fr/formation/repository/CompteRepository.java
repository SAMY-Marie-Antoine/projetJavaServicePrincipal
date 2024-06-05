package fr.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Compte;

public interface CompteRepository extends JpaRepository<Compte, String> {
    
}
