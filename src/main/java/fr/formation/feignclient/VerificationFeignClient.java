package fr.formation.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

// @FeignClient(value = "commentaire-service", url = "http://localhost:8082", path = "/api/commentaire")
// @FeignClient(value = "commentaire-service", path = "/api/commentaire")

// Déclaration du client Feign avec fallback en cas de défaillance du service de vérification
@FeignClient(value = "projetJavaServiceVerification", path = "/api/verification", fallback = VerificationFeignClient.Fallback.class)
public interface VerificationFeignClient {
    
    @GetMapping("/{id}")
    public String getMotDePasseById(@PathVariable String id);

    @GetMapping("/mot-de-passe/vulnerable/{motDePasse}")
    String getMotDePasseVulnerableById(@PathVariable("motDePasse") String motDePasse);

    @GetMapping("/mot-de-passe/force")
    int getForceMotDePasse(@RequestParam("motDePasse") String motDePasse);


    @Component
    public static class Fallback implements VerificationFeignClient {
        
        @Override
        public String getMotDePasseById(String id) {
            return null;
            //return "Service verification indisponible";
        }

        @Override
        public String getMotDePasseVulnerableById(String motDePasse) {
            return null;
        }

        @Override
        public int getForceMotDePasse(String motDePasse) {
            return -1;
        }
    }
}
