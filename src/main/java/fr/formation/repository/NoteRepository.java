package fr.formation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Note;
import jakarta.validation.Valid;

public interface NoteRepository extends JpaRepository<Note, String> {

    List<Note> findByUtilisateurId(String userId);

    List<Note> findByNom(@Valid String nom);
    
}
