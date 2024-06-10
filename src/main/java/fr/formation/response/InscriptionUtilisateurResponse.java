package fr.formation.response;

import java.time.LocalDate;
import java.util.List;

import fr.formation.model.Compte;
import fr.formation.model.Note;

public class InscriptionUtilisateurResponse {

	private String id;
	private String nom;
	private LocalDate dateDeNaissance;
	private String email;
	private String motDePasse;
	private String confirmMotDePasse;
	private int forceMotDePasse;

	private List<Note> notes; // Ajoutez cette ligne
	private List<Compte> comptes; // Ajoutez cette ligne
	
	public InscriptionUtilisateurResponse() {
		
	}
	
	public InscriptionUtilisateurResponse(String id) {
		this.id = id;
	}
	
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
	public LocalDate getDateDeNaissance() {
		return dateDeNaissance;
	}
	public void setDateDeNaissance(LocalDate dateDeNaissance) {
		this.dateDeNaissance = dateDeNaissance;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMotDePasse() {
		return motDePasse;
	}
	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}
	
	public String getConfirmMotDePasse() {
		return confirmMotDePasse;
	}
	public void setConfirmMotDePasse(String confirmMotDePasse) {
		this.confirmMotDePasse = confirmMotDePasse;
	}

	public int getForceMotDePasse() {
		return forceMotDePasse;
	}

	public void setForceMotDePasse(int forceMotDePasse) {
		this.forceMotDePasse = forceMotDePasse;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public List<Compte> getComptes() {
		return comptes;
	}

	public void setComptes(List<Compte> comptes) {
		this.comptes = comptes;
	}
	

}
