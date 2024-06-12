package fr.formation.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MotDePasseService {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MotDePasseService() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public String crypterMotDePasse(String motDePasse) {
        return bCryptPasswordEncoder.encode(motDePasse);
    }

}
