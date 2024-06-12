package fr.formation.response;

import java.time.LocalDateTime;

import fr.formation.model.Utilisateur;

public class CompteResponse {

	private String id;
	private String nom;
	private String description;
	private LocalDateTime dateAjout;
	private LocalDateTime dateMAJ;
	private String nomUtilisateurPlateforme;
	private String urlPlateforme;
	private String valeurMotdePassePlateforme;
	private String cle;
	public String getCle() {
		return cle;
	}

	public void setCle(String cle) {
		this.cle = cle;
	}

	//private Utilisateur utilisateur; //eviter le pb de ref circulaire de la serialisation JSON
	private String utilisateurId;
	
	public CompteResponse() {
	}
	
	public CompteResponse(String id) {
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
