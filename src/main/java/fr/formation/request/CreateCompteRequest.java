package fr.formation.request;


import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CreateCompteRequest {

	@NotNull
	@NotBlank(message = "Le nom ne peut pas être vide")
	private String nom;
	private String description;
	
	@NotNull(message = "La date d'ajout ne peut pas être nulle")
	private LocalDateTime dateAjout;
	private LocalDateTime dateMAJ;

	private String nomUtilisateurPlateforme;
	private String urlPlateforme;

	@NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(max = 512, message = "Le mot de passe ne peut pas dépasser 512 caractères")
	private String valeurMotdePassePlateforme;
	
	private Utilisateur utilisateur;
	
	
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
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
	public LocalDateTime getDateMAJ() {
		return dateMAJ;
	}
	public void setDateMAJ(LocalDateTime dateMAJ){
		this.dateMAJ = dateMAJ;
	}
	public String getNomUtilisateurPlateforme() {
		return nomUtilisateurPlateforme;
	}
	public void setNomUtilisateurPlateforme(String nomUtilisateurPlateforme) {
		this.nomUtilisateurPlateforme = nomUtilisateurPlateforme;
	}
	public String getUrlPlateforme() {
		return urlPlateforme;
	}
	public void setUrlPlateforme(String urlPlateforme) {
		this.urlPlateforme = urlPlateforme;
	}
	public String getValeurMotdePassePlateforme() {
		return valeurMotdePassePlateforme;
	}
	public void setValeurMotdePassePlateforme(String valeurMotdePassePlateforme) {
		this.valeurMotdePassePlateforme = valeurMotdePassePlateforme;
	}


}
