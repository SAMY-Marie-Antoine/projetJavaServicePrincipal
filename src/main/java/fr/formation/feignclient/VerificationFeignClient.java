package fr.formation.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// @FeignClient(value = "commentaire-service", url = "http://localhost:8082", path = "/api/commentaire")
// @FeignClient(value = "commentaire-service", path = "/api/commentaire")
@FeignClient(value = "projetJavaServiceVerification", path = "/api/verification", fallback = VerificationFeignClient.Fallback.class)
public interface VerificationFeignClient {
    @GetMapping("/{id}")
    public String getMotDePasseById(@PathVariable String id);

    @Component
    public static class Fallback implements VerificationFeignClient {
        @Override
        public String getMotDePasseById(String id) {
            return null;
        }
    }
}
