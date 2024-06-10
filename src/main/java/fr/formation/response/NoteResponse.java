package fr.formation.response;

import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;


public class NoteResponse {

	private String id;
	private String nom;
	private String description;
	private LocalDateTime dateAjout;
	private LocalDateTime dateModif;
	private String contenu;
	private String utilisateurId; 
	//private Utilisateur utilisateur;
		
	public NoteResponse() {
	}
	
	public NoteResponse(String id) {
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
	/*public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}*/

	public String getUtilisateurId() {
		return utilisateurId;
	}

	public void setUtilisateurId(String utilisateurId) {
		this.utilisateurId = utilisateurId;
	}

	



}
