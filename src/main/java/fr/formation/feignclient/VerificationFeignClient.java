package fr.formation.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


// @FeignClient(value = "commentaire-service", url = "http://localhost:8082", path = "/api/commentaire")
// @FeignClient(value = "commentaire-service", path = "/api/commentaire")

// Déclaration du client Feign avec fallback en cas de défaillance du service de vérification
@FeignClient(value = "projetJavaServiceVerification", path = "/api/verification", fallback = VerificationFeignClient.Fallback.class)
public interface VerificationFeignClient {
    
	@PostMapping("/generateMotDePasseFort")
	public String generateMotDePasseFort();
	    
	@PostMapping("/mot-de-passe/compromis")
	boolean getMotDePasseCompromis(@RequestBody String motDePasse);

	@PostMapping("/mot-de-passe/force")
	boolean getForceMotDePasse(@RequestBody String motDePasse);

    @Component
    public static class Fallback implements VerificationFeignClient {

		@Override
		public String generateMotDePasseFort() {
			
			return null;
		}

		@Override
		public boolean getMotDePasseCompromis(String motDePasse) {
			
			return false;
		}

		@Override
		public boolean getForceMotDePasse(String motDePasse) {
		
			return false;
		}

        
       
    }
}
