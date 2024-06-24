package fr.formation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "compte")
public class Compte {

	@Id
	@UuidGenerator
	private String id;
	
	@Column(name="nom", nullable = false)
	private  String nom;
	
	@Column(name="description")
	private  String description;
		
	@Column(name="date_ajout")
	private LocalDateTime dateAjout;
	
	@Column(name="date_maj")
	private LocalDateTime dateMAJ;
	
	@Column(name="nom_utilisateur_plateforme;")
	private String nomUtilisateurPlateforme;
	
	@Column(name="url_plateforme")
	private String urlPlateforme;
	
	@Column(name="valeur_motde_passe_plateforme", length = 512, nullable = false)
	private String valeurMotdePassePlateforme;
	
	private String cle;
	

	//un compte est associé à un utilisateur
	@ManyToOne()
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
	public LocalDateTime getDateAjout() {
		return dateAjout;
	}
	public void setDateAjout(LocalDateTime dateAjout) {
		this.dateAjout = dateAjout;
	}
	public LocalDateTime getDateMAJ() {
		return dateMAJ;
	}
	public void setDateMAJ(LocalDateTime dateMAJ) {
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
	public Utilisateur getUtilisateur() {
		return utilisateur;
	}
	public void setUtilisateur(Utilisateur utilisateur) {
		this.utilisateur = utilisateur;
	}
	public String getCle() {
		return cle;
	}
	public void setCle(String cle) {
		this.cle = cle;
	}
	
}
