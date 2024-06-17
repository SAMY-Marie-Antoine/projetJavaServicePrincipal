package fr.formation.model;

import java.time.LocalDate;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

	@Id
	@UuidGenerator
	private String id;

	@Column(name="nom")
	private String nom;

	@Column(name="date_de_naissance")
	private LocalDate dateDeNaissance; 

	@Column(name="email", nullable = false)
	private String email;

	@Column(name="mot_de_passe", length = 512, nullable = false)
	private String motDePasse;

	@Transient
	private String confirmMotDePasse;



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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	public void setDateDeNaissance(LocalDate dateDeNaissance) {
		this.dateDeNaissance = dateDeNaissance;
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
