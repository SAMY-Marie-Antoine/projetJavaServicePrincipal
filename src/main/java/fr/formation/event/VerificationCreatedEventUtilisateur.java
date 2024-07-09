package fr.formation.event;

import java.time.LocalDateTime;

public class VerificationCreatedEventUtilisateur {
	private String message;
	private String nom;
	private String password;
	private String Level;
	private LocalDateTime timestamp;
	
    public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getLevel() {
		return Level;
	}
	public void setLevel(String Level) {
		this.Level = Level;
	}
	private String UtilisateurId;
    
    
    public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUtilisateurId() {
		return UtilisateurId;
	}
	public void setUtilisateurId(String utilisateurId) {
		UtilisateurId = utilisateurId;
	}
    
    
}
