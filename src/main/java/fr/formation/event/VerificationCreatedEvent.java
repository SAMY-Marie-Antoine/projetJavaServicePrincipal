package fr.formation.event;

public class VerificationCreatedEvent {
    private String VerificatonId;
    private String UtilisateurId;
    
	public String getVerificatonId() {
		return VerificatonId;
	}
	public void setVerificatonId(String verificatonId) {
		VerificatonId = verificatonId;
	}
	public String getUtilisateurId() {
		return UtilisateurId;
	}
	public void setUtilisateurId(String utilisateurId) {
		UtilisateurId = utilisateurId;
	}
    
    
}
