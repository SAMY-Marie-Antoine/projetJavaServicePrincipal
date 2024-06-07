package fr.formation.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public class InscriptionUtilisateurRequest {
	@NotBlank
    private String nom;
	@NotBlank
    private LocalDate dateDeNaissance;
	@NotBlank
  	private String email;
	@NotBlank
  	private String motDePasse;
	
  	private String confirmMotDePasse;

	
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
