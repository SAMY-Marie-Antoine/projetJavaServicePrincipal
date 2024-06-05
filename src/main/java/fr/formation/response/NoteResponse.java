package fr.formation.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import fr.formation.model.Utilisateur;


public class NoteResponse {


	private String nom;
	private BigDecimal description;
	private LocalDate dateAjout;
	private LocalDate dateModif;
	private String contenu;
	private Utilisateur utilisateur;

	private String id;
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
	public BigDecimal getDescription() {
		return description;
	}
	public void setDescription(BigDecimal description) {
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
