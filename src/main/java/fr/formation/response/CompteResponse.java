package fr.formation.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CompteResponse {

	private String id;
	private String nom;
	private String description;
	private LocalDate dateAjout;
	private LocalDate dateMAJ;
	private String nomUtilisateurPlateforme;
	private String urlPlateforme;
	private BigDecimal valeurMotdePassePlateforme;

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

}
