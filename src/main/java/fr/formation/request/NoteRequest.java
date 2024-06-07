package fr.formation.request;

import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;


public class NoteRequest {
	
    private String nom;
    private String description;
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
