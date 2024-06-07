package fr.formation.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;


public class NoteResponse {

	private String id;
	private String nom;
	private String description;
	private LocalDateTime dateAjout;
	private LocalDateTime dateModif;
	private String contenu;
	private Utilisateur utilisateur;

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
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
	public LocalDate getDateAjout() {
		return dateAjout;
	}
	public void setDateAjout(LocalDate dateAjout) {
		this.dateAjout = dateAjout;
	}
	public LocalDate getDateModif() {
		return dateModif;
	}
	public void setDateModif(LocalDate dateModif) {
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
