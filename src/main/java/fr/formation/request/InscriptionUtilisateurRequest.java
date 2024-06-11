package fr.formation.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InscriptionUtilisateurRequest {
	@NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;
	
	@NotNull(message = "La date de naissance ne peut pas être nulle")
    private LocalDate dateDeNaissance;

	@NotBlank(message = "L'email ne peut pas être vide")
	@Email(message = "L'email doit être valide")
	private String email;
	
	@NotBlank
  	private String motDePasse;
	
  	private String confirmMotDePasse;

  	public InscriptionUtilisateurRequest() {
		// TODO Auto-generated constructor stub
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
}
