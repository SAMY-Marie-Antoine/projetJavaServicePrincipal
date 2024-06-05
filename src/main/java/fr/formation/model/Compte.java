package fr.formation.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.UuidGenerator;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "compte")
public class Compte {

	@Id
	@UuidGenerator
	private String id;
	
	@Column(nullable = false)
	private  String nom;
	
	@Column(name="description")
	private  String description;
	
	@Temporal(TemporalType.DATE)
	@Column(name="date_ajout")
	private LocalDate dateAjout;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.INSERT)
	@Column(name="date_maj")
	private LocalDate dateMAJ;
	
	@Column
	private String nomUtilisateurPlateforme;
	
	@Column
	private String urlPlateforme;
	
	@Column(nullable = false)
	private BigDecimal valeurMotdePassePlateforme;
	
	private Utilisateur utilisateur ;
	
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
	public LocalDate getDateMAJ() {
		return dateMAJ;
	}
	public void setDateMAJ(LocalDate dateMAJ) {
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
	public BigDecimal getValeurMotdePassePlateforme() {
		return valeurMotdePassePlateforme;
	}
	public void setValeurMotdePassePlateforme(BigDecimal valeurMotdePassePlateforme) {
		this.valeurMotdePassePlateforme = valeurMotdePassePlateforme;
	}
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}
	
	

}
