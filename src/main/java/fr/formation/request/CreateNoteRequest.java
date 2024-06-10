package fr.formation.request;

import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CreateNoteRequest {
	
	@NotNull
	@NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;
	
	@Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

	@NotNull(message = "La date d'ajout ne peut pas être nulle")
    private LocalDateTime dateAjout;

    private LocalDateTime dateModif;
  	private String contenu;
	
	private Utilisateur utilisateur;
	
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LocalDateTime getDateAjout() {
		return dateAjout;
	}
	public void setDateAjout(LocalDateTime dateAjout) {
		this.dateAjout = dateAjout;
	}
	public LocalDateTime getDateModif() {
		return dateModif;
	}
	public void setDateModif(LocalDateTime dateModif) {
		this.dateModif = dateModif;
	}
	public String getContenu() {
		return contenu;
	}
	public void setContenu(String contenu) {
		this.contenu = contenu;
	}
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}
	
	

    
}
