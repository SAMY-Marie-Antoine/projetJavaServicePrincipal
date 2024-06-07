package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.formation.model.Compte;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CreateCompteRequest;
import fr.formation.response.CompteResponse;


@RestController
@RequestMapping("/api/compte")
@CrossOrigin("*")
public class CompteApiController {

	private static final Logger log = LoggerFactory.getLogger(CompteApiController.class);

	@Autowired
	private CompteRepository compteRepository;

	@Autowired
	//private VerificationFeignClient commentaireFeignClient;

	
	public CompteApiController(CompteRepository compteRepository) {

		this.compteRepository = compteRepository;
		log.info("Initialisation de CompteApiController");
	}

	@GetMapping
	public List<CompteResponse> findAll() {

		log.info("Recherche de tous les comptes");

		List<Compte> comptes = this.compteRepository.findAll();
		List<CompteResponse> response = new ArrayList<>();

		for (Compte compte : comptes) {
			CompteResponse compteResponse = new CompteResponse();
			BeanUtils.copyProperties(compte, compteResponse);
			response.add(compteResponse);
		}

		log.info("Liste de tous les comptes récupérée avec succès");
		return response;
	}


	@GetMapping("/{id}/name")
	public String getNameById(@PathVariable String id) {

		log.info("Recherche du nom du compte avec l'ID : {}", id);
		Optional<Compte> optCompte = this.compteRepository.findById(id);

		if (optCompte.isPresent()) {
			log.info("Nom du compte trouvé pour l'ID : {}", id);
			return optCompte.get().getNom();
		}
		log.warn("Aucun compte trouvé pour l'ID : {}", id);
		return "- vompte non trouvé -";
	}



	@GetMapping("/{id}")
	public Compte findById(@PathVariable("id") String id) {
		log.info("Recherche du compte avec l'ID : {}", id);
		Optional<Compte> compte = this.compteRepository.findById(id);

		if (compte.isEmpty()) {
			log.warn("Aucun compte trouvé pour l'ID : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Compte inexistant");
		}

		log.info("Compte trouvé pour l'ID : {}", id);
		return compte.get();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String update(@PathVariable("id") String id,@RequestBody CreateCompteRequest request) {
		
		log.info("Mise à jour du compte avec l'ID : {}", id);
		
		Compte comptebdd=this.compteRepository.findById(id).get();
		Compte compte = new Compte();
		BeanUtils.copyProperties(request, comptebdd);

		this.compteRepository.save(comptebdd);

        log.info("Compte mis à jour avec succès pour l'ID : {}", id);
		return compte.getId();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String delete(@PathVariable("id") String id) {
		
		log.info("Suppression du compte avec l'ID : {}", id);
		
		Optional<Compte> comptebdd=this.compteRepository.findById(id);
		
		if (comptebdd.isEmpty()) {
			log.error("Compte non trouvée avec l'id : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Note inexistant");
		}

		
		this.compteRepository.deleteById(id);

        log.info("Compte supprimé avec succès pour l'ID : {}", id);
		return id;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@RequestBody CreateCompteRequest request) {
		
		log.info("Création d'un nouveau compte");
		Compte compte = new Compte();

		BeanUtils.copyProperties(request, compte);

		this.compteRepository.save(compte);

		log.info("Nouveau compte créé avec succès, ID : {}", compte.getId());
		return compte.getId();
	}

	//lister les comptes d'un utilisateur spécifique
    @GetMapping("/user/{userId}")
    public List<CompteResponse> findByUserId(@PathVariable String userId) {
		
		log.info("Recherche des comptes pour l'utilisateur avec l'ID : {}", userId);
        
		List<Compte> comptes = this.compteRepository.findByUtilisateurId(userId);
        List<CompteResponse> response = new ArrayList<>();

        for (Compte compte : comptes) {
            CompteResponse compteResponse = new CompteResponse();
            BeanUtils.copyProperties(compte, compteResponse);
            response.add(compteResponse);
        }

		log.info("Comptes de l'utilisateur avec l'ID : {} récupérés avec succès", userId);
        return response;
    }
}
