package fr.formation.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// @FeignClient(value = "commentaire-service", url = "http://localhost:8082", path = "/api/commentaire")
// @FeignClient(value = "commentaire-service", path = "/api/commentaire")

// Déclaration du client Feign avec fallback en cas de défaillance du service de vérification
@FeignClient(value = "projetJavaServiceVerification", path = "/api/verification", fallback = VerificationFeignClient.Fallback.class)
public interface VerificationFeignClient {
    
	@GetMapping("/generateMotDePasseFort")
	public String generateMotDePasseFort();
	    
	@PostMapping("/mot-de-passe/compromis")
	boolean getMotDePasseCompromis(@RequestBody String motDePasse);

	@PostMapping("/mot-de-passe/force")
	boolean getForceMotDePasse(@RequestBody String motDePasse);

    @Component
    public static class Fallback implements VerificationFeignClient {

		@Override
		public String generateMotDePasseFort() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getMotDePasseCompromis(String motDePasse) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean getForceMotDePasse(String motDePasse) {
			// TODO Auto-generated method stub
			return false;
		}

        
       
    }
}
