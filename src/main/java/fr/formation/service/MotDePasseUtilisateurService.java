package fr.formation.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class MotDePasseUtilisateurService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public MotDePasseUtilisateurService() {
		this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
	}

	public String crypterMotDePasse(String motDePasse) {
		return bCryptPasswordEncoder.encode(motDePasse);
	}

}


