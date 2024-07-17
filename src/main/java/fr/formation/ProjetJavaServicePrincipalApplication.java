package fr.formation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients // Activer les clients Feign
@EnableDiscoveryClient // Activer la découverte de services
public class ProjetJavaServicePrincipalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetJavaServicePrincipalApplication.class, args);
	}

}
