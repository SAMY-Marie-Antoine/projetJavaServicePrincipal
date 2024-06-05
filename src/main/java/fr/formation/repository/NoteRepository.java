package fr.formation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.formation.model.Note;

public interface NoteRepository extends JpaRepository<Note, String> {
    
}
