package fr.formation.request;

import java.math.BigDecimal;
import java.time.LocalDate;


public class CompteRequest {

	private String nom;
	private BigDecimal description;
	private LocalDate dateAjout;
	private LocalDate dateMAJ;
	private String nomUtilisateurPlateforme;
	private String urlPlateforme;
	private BigDecimal valeurMotdePassePlateforme;

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
