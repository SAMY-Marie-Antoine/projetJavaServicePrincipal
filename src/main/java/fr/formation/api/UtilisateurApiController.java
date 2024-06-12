package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import fr.formation.feignclient.VerificationFeignClient;
import fr.formation.model.Compte;
import fr.formation.model.Note;
import fr.formation.model.Utilisateur;
import fr.formation.repository.CompteRepository;
import fr.formation.repository.NoteRepository;
import fr.formation.repository.UtilisateurRepository;
import fr.formation.request.InscriptionUtilisateurRequest;
import fr.formation.request.LoginUtilisateurRequest;
import fr.formation.response.InscriptionUtilisateurResponse;
import fr.formation.service.MotDePasseService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/utilisateur")
@CrossOrigin("*")
public class UtilisateurApiController {

	private static final Logger log = LoggerFactory.getLogger(UtilisateurApiController.class);

	
	private final UtilisateurRepository utilisateurRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private CompteRepository compteRepository;

	@Autowired
	private VerificationFeignClient verificationFeignClient;

	@Autowired
	private MotDePasseService motDePasseService; 

	//@Autowired //a enlever selon notre decision 
	public UtilisateurApiController(UtilisateurRepository utilisateurRepository) {
		this.utilisateurRepository = utilisateurRepository;
		log.info("Initialisation de UtilisateurApiController");
	}

	@GetMapping
	public List<InscriptionUtilisateurResponse> findAll() {

		log.info("Exécution de la méthode findAll");

		List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
		List<InscriptionUtilisateurResponse> response = new ArrayList<>();

		for (Utilisateur utilisateur : utilisateurs) {
			InscriptionUtilisateurResponse utilisateurResponse = new InscriptionUtilisateurResponse();
			BeanUtils.copyProperties(utilisateur, utilisateurResponse);
			
			// Appel à serviceVerification pour obtenir la force du mot de passe
			boolean forceMotDePasse = this.verificationFeignClient.getForceMotDePasse(utilisateur.getMotDePasse());
			
			// Ajoutez la force du mot de passe à la réponse
			utilisateurResponse.setForceMotDePasse(forceMotDePasse);

			// Récupérer les notes sécurisées de l'utilisateur
			List<Note> notes = this.noteRepository.findByUtilisateurId(utilisateur.getId());
			utilisateurResponse.setNotes(notes);

			// Récupérer les comptes de l'utilisateur
			List<Compte> comptes = this.compteRepository.findByUtilisateurId(utilisateur.getId());
			utilisateurResponse.setComptes(comptes);

			response.add(utilisateurResponse);
    }

    log.info("La méthode findAll a été exécutée avec succès");
    return response;
	}


	@GetMapping("/{id}/name")
	public String getNameById(@Valid @PathVariable String id) {

		log.info("Exécution de la méthode getNameById avec l'id: " + id);		
		
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findById(id);

		if (optUtilisateur.isPresent()) {

			log.info("La méthode getNameById a été exécutée avec succès");			
			return optUtilisateur.get().getNom();
		}

		log.warn("Utilisateur non trouvé dans la méthode getNameById avec l'id: " + id);
		return "- Utilisateur non trouvé -";
	}

	@GetMapping("/{id}")
	public Utilisateur findById(@Valid @PathVariable("id") String id) {

		log.info("Exécution de la méthode findById avec l'id: " + id);
		Optional<Utilisateur> utilisateur = this.utilisateurRepository.findById(id);

		if (utilisateur.isEmpty()) {
			log.warn("Utilisateur non trouvé dans la méthode findById avec l'id: " + id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Utilisateur inexistant");
		}

		log.info("La méthode findById a été exécutée avec succès");
		return utilisateur.get();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String update(@Valid @PathVariable("id") String id,@RequestBody InscriptionUtilisateurRequest request) {

		log.info("Exécution de la méthode update avec l'id: " + id);
		
		Utilisateur utilisateurbdd = this.utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Utilisateur inexistant"));

		
		BeanUtils.copyProperties(request, utilisateurbdd);

		this.utilisateurRepository.save(utilisateurbdd);

		log.info("La méthode update a été exécutée avec succès");
		return utilisateurbdd.getId();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String delete(@Valid @PathVariable("id") String id,@RequestBody InscriptionUtilisateurRequest request) {

		log.info("Exécution de la méthode delete avec l'id: " + id);

		Optional<Utilisateur> utilisateurbdd=this.utilisateurRepository.findById(id);
		Utilisateur utilisateur = new Utilisateur();
		BeanUtils.copyProperties(request, utilisateurbdd);

		this.utilisateurRepository.deleteById(id);

		log.info("La méthode delete a été exécutée avec succès");
		return utilisateur.getId();
	}


	@PostMapping("/connexion")
	public Utilisateur connexion(@Valid @RequestBody LoginUtilisateurRequest request) {
		
		log.info("Exécution de la méthode connexion");
		
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndMotDePasse(request.getEmail(), request.getMotDePasse());
		
		if(optUtilisateur.isEmpty()) {
			log.warn("Utilisateur non trouvé dans la méthode connexion");

			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	
		if(!optUtilisateur.get().getEmail().equals(request.getEmail()) ) {

			log.warn("Email inexistant dans la méthode connexion");

			System.out.println("email inexistant !" + optUtilisateur.get().getEmail());

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
				
		if(!optUtilisateur.get().getMotDePasse().equals(request.getMotDePasse())) {
			
			log.warn("Mot de passe incorrect dans la méthode connexion");
			System.out.println("mot de passe incorrect");

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			
		}
				
		log.info("La méthode connexion a été exécutée avec succès");
		return optUtilisateur.get();
	}

	@PostMapping("/inscription")
	@ResponseStatus(HttpStatus.CREATED)
	public InscriptionUtilisateurResponse inscription(@Valid @RequestBody InscriptionUtilisateurRequest request) {
		
		log.info("Exécution de la méthode inscription avec les détails: {}", request);

        Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmail(request.getEmail());
        if (optUtilisateur.isPresent()) {
            log.warn("Email déjà existant dans la méthode inscription");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email déjà existant");
        }

        if (!request.getMotDePasse().equals(request.getConfirmMotDePasse())) {
            log.warn("La confirmation du mot de passe ne correspond pas dans la méthode inscription");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La confirmation du mot de passe ne correspond pas");
        }

        // Utilisation de Feign pour vérifier la vulnérabilité du mot de passe
       boolean motDePasseCompromis=this.verificationFeignClient.getMotDePasseCompromis(request.getMotDePasse());
       if (!motDePasseCompromis ) {
           log.warn("Le mot de passe est compromis dans la méthode inscription");
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est compromis");
       }

        // Utilisation de Feign pour vérifier la force du mot de passe
       boolean motDePasseForce=this.verificationFeignClient.getForceMotDePasse(request.getMotDePasse());
       if (!motDePasseForce ) {
           log.warn("Le mot de passe est faible dans la méthode inscription");
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est faible");
       }

        Utilisateur utilisateur = new Utilisateur();
        BeanUtils.copyProperties(request, utilisateur);
		
		// Crypter le mot de passe avant de l'enregistrer
        utilisateur.setMotDePasse(motDePasseService.crypterMotDePasse(request.getMotDePasse()));

        this.utilisateurRepository.save(utilisateur);

        log.info("La méthode inscription a été exécutée avec succès");
        return new InscriptionUtilisateurResponse(utilisateur.getId());
    }

}
