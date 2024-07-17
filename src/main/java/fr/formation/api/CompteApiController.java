package fr.formation.api;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.DependsOn;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.formation.event.CompteEvent;
import fr.formation.feignclient.VerificationFeignClient;
import fr.formation.model.Compte;
import fr.formation.model.Utilisateur;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CreateCompteRequest;
import fr.formation.response.CompteResponse;
import fr.formation.service.ValeurMotDePasseCompteService;
import fr.formation.service.ValeurMotDePasseCompteServiceDecryptage;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/compte")
//@CrossOrigin("*")(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://localhost:4200",methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.PUT})
public class CompteApiController {

	private static final Logger log = LoggerFactory.getLogger(CompteApiController.class);

	@Autowired
	private CompteRepository compteRepository;

	@Autowired
	private VerificationFeignClient verificationFeignClient;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	private ValeurMotDePasseCompteService valeurMotDePasseCompteService;


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
	public String getNameById(@Valid @PathVariable String id) {

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
	public Compte findById(@Valid @PathVariable("id") String id) {
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
	public ResponseEntity<String> update(@Valid @PathVariable("id") String id,@RequestBody CreateCompteRequest request) {

		// Vérification de l'existence du compte
		Optional<Compte> optionalCompte = this.compteRepository.findById(id);
		if (!optionalCompte.isPresent()) {
			// Si le compte n'existe pas, créer un nouvel événement de journalisation
			CompteEvent event = new CompteEvent();
			event.setLevel("ERROR");
			event.setMessage("Le compte avec l'ID : " + id + " n'existe pas.");
			event.setTimestamp(LocalDateTime.now());
			// Envoi de l'événement à l'écouteur approprié
			streamBridge.send("compte.errorUpdated", event);
			log.error(event.getMessage());
			// Retourner une chaîne d'erreur
			return new ResponseEntity<>(event.getMessage(), HttpStatus.NOT_FOUND);
		}

		// Création d'un nouvel objet CompteEvent pour la journalisation
		CompteEvent event1 = new CompteEvent();
		event1.setLevel("INFO");
		event1.setMessage("Mise à jour du compte avec l'ID : " + id);
		event1.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement à l'écouteur approprié
		streamBridge.send("compte.updated", event1);

		log.info(event1.getMessage());

		Compte comptebdd=this.compteRepository.findById(id).get();
		
		BeanUtils.copyProperties(request, comptebdd);
		// mettre à jour la date de modification
		comptebdd.setDateMAJ(LocalDateTime.now());

		this.compteRepository.save(comptebdd);

		// Création d'un nouvel objet CompteEvent pour la journalisation
		CompteEvent event2 = new CompteEvent();
		event2.setLevel("INFO");
		event2.setMessage("Compte mis à jour avec succès pour l'ID : " + id);
		event2.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement à l'écouteur approprié
		streamBridge.send("compte.updated", event2);
	
		log.info(event2.getMessage());
	
		// new ResponseEntity<>(comptebdd.getId(), HttpStatus.OK) crée une nouvelle réponse 
		//avec le corps comptebdd.getId() et le statut HTTP 200 (OK).
		return new ResponseEntity<>(comptebdd.getId(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // Indique que la suppression a réussi avec un code de statut 204
	public void delete(@Valid @PathVariable("id") String id) {

		CompteEvent event = new CompteEvent();

		// Définition du niveau de l'événement
		event.setLevel("INFO");
		// Définition du message de l'événement
		event.setMessage("Suppression du compte avec l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement au StreamBridge
		streamBridge.send("compte.deleted", event);
		log.info(event.getMessage());

		Optional<Compte> comptebdd=this.compteRepository.findById(id);

		if (comptebdd.isEmpty()) {

			// Mise à jour du message de l'événement
			event.setMessage("Compte non trouvé avec l'ID : " + id);
			event.setTimestamp(LocalDateTime.now());
			// Envoi du nouvel événement au StreamBridge
			streamBridge.send("compte.deleted", event);
			log.warn(event.getMessage());
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Compte inexistant");
		}

		this.compteRepository.deleteById(id);

		// Mise à jour du message de l'événement
		event.setMessage("Compte supprimé avec succès pour l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi du nouvel événement au StreamBridge
		streamBridge.send("compte.deleted", event);
		log.info(event.getMessage());
		//return id;
	}

	 
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@Valid @RequestBody CreateCompteRequest request) throws Exception {

		// Ajoutez le code de vérification ici
		/*if (request.getUtilisateur() == null) {
			log.error("L'objet Utilisateur est null");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'objet Utilisateur est null");
		} else if (request.getUtilisateur().getId() == null) {
			log.error("L'ID de l'utilisateur est null");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'ID de l'utilisateur est null");
		}*/

		// Création d'un nouvel objet CompteEvent pour la journalisation
		CompteEvent event3 = new CompteEvent();
		event3.setLevel("INFO");
		event3.setMessage("Création d'un nouveau compte");
		event3.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement à l'écouteur approprié
		streamBridge.send("compte.created", event3);

		log.info("Création d'un nouveau compte");
		log.info("Exécution de la méthode Creation du Compte avec les détails: {}", request);
		
		// Utilisation de Feign pour vérifier la vulnérabilité du mot de passe
		boolean motDePasseCompromis=this.verificationFeignClient.getMotDePasseCompromis(request.getValeurMotdePassePlateforme());
		// Si le mot de passe est compromis, on envoie un avertissement et on lance une exception
		if (motDePasseCompromis ) {

			CompteEvent event4 = new CompteEvent();
			event4.setLevel("WARN");
			event4.setMessage("Le mot de passe est compromis dans la méthode inscription et le compte n'est pas crée");
			event4.setTimestamp(LocalDateTime.now());
			streamBridge.send("compte.rejected", event4);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est compromis et le compte n'est pas crée");
		}

		// Utilisation de Feign pour vérifier la force du mot de passe
		boolean motDePasseForce=this.verificationFeignClient.getForceMotDePasse(request.getValeurMotdePassePlateforme());
		if (!motDePasseForce ) {

			CompteEvent event5 = new CompteEvent();
			event5.setLevel("WARN");
			event5.setMessage("Le mot de passe est faible dans la méthode inscription et le compte n'est pas crée");
			event5.setTimestamp(LocalDateTime.now());
			streamBridge.send("compte.rejected", event5);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est faible et le compte n'est pas crée");
		}

		Compte compte = new Compte();

		/*if (!request.getUtilisateur().getId().isEmpty()) {
			compte.setId(request.getUtilisateur().getId());
		}*/
		
		// Copie des propriétés de la requête dans le compte
		BeanUtils.copyProperties(request, compte);

		compte.setId(request.getUtilisateur().getId()); // H 03/7 21h
		compte.setDateAjout(LocalDateTime.now());
		compte.setDateMAJ(LocalDateTime.now());
		
		// Chiffrement du mot de passe
		byte[] codedtext = new ValeurMotDePasseCompteService().encrypt(request.getValeurMotdePassePlateforme());
		// Décodage du mot de passe chiffré (pour vérification)
		String decodedtext = new ValeurMotDePasseCompteServiceDecryptage().decrypt(codedtext);
		// Mise à jour du mot de passe chiffré dans le compte
		compte.setValeurMotdePassePlateforme(Base64.getEncoder().encodeToString(codedtext));
		
		// Sauvegarde du compte dans la base de données
		this.compteRepository.save(compte);

		// Journalisation de la création réussie du compte
		CompteEvent event = new CompteEvent();
		event.setLevel("INFO");
		event.setMessage("Nouveau compte créé avec succès, ID : " + compte.getId());
		event.setTimestamp(LocalDateTime.now());
		log.info(event.getMessage());
		streamBridge.send("compte.created", event);
		
		// Retour de l'ID du compte créé
		return compte.getId();
	}


	@GetMapping("cryptage/{id}")
	public String decypt(@Valid @PathVariable("id") String id) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		CompteEvent event = new CompteEvent();

		// Définition du niveau de l'événement
		event.setLevel("INFO");
		// Définition du message de l'événement
		event.setMessage("Recherche du compte avec l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement au StreamBridge
		streamBridge.send("compte.decrypted", event);
		log.info(event.getMessage());

		log.info("Recherche du compte avec l'ID : {}", id);
		Optional<Compte> optCompte = this.compteRepository.findById(id);

		if (optCompte.isEmpty()) {
			// Mise à jour du message de l'événement
			event.setMessage("Aucun compte trouvé pour l'ID : " + id);
			event.setTimestamp(LocalDateTime.now());
			// Envoi du nouvel événement au StreamBridge
			streamBridge.send("compte.decrypted", event);
			log.warn(event.getMessage());

			log.warn("Aucun compte trouvé pour l'ID : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Compte inexistant");
		}

		//Traitement conversion de la cle Sring BDD vers SecretKey
		//SecretKey cleInter=ValeurMotDePasseCompteServiceDecryptage.convertStringToSecretKeyto(optCompte.get().getCle());
		//Traitement conversion de la ValeurMotdePassePlateforme Sring BDD vers Byte
		//byte[] donnees=optCompte.get().getValeurMotdePassePlateforme().getBytes();
		//Traitement decryptage de la ValeurMotdePassePlateforme		
		//String motDePasseDecrypt=ValeurMotDePasseCompteServiceDecryptage.decrypter(donnees, cleInter);

		// Mise à jour du message de l'événement
		event.setMessage("Compte trouvé pour l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi du nouvel événement au StreamBridge
		streamBridge.send("compte.decrypted", event);
		log.info(event.getMessage());

		log.info("Compte trouvé pour l'ID : {}", id);
		return null;
	}


	//lister les comptes d'un utilisateur spécifique
	@GetMapping("/user/{userId}")
	public List<CompteResponse> findByUserId(@Valid @PathVariable String userId) {
		
		CompteEvent event = new CompteEvent();
		// Définition du niveau de l'événement
		event.setLevel("INFO");
		// Définition du message de l'événement
		event.setMessage("Recherche des comptes pour l'utilisateur avec l'ID : " + userId);
		// Ajout de l'horodatage à l'événement
		event.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement au StreamBridge
		streamBridge.send("compte.userRetrieved", event);
		log.info(event.getMessage());

		log.info("Recherche des comptes pour l'utilisateur avec l'ID : {}", userId);
	
		List<Compte> comptes = this.compteRepository.findByUtilisateurId(userId);
		List<CompteResponse> response = new ArrayList<>();

		for (Compte compte : comptes) {
			CompteResponse compteResponse = new CompteResponse();    
			BeanUtils.copyProperties(compte, compteResponse);
			response.add(compteResponse);
		}

		// Mise à jour du message de l'événement
		event.setMessage("Comptes de l'utilisateur avec l'ID : " + userId + " récupérés avec succès");
		event.setTimestamp(LocalDateTime.now());
		// Envoi du nouvel événement au StreamBridge
		streamBridge.send("compte.userRetrieved", event);
		log.info(event.getMessage());

		log.info("Comptes de l'utilisateur avec l'ID : {} récupérés avec succès", userId);
		return response;
	}

	@GetMapping("/by-name/{nom}")
	public List<Compte> getCompteByName(@Valid @PathVariable String nom) {
		log.info("Recherche des comptes avec le nom : {}", nom);
		List<Compte> comptes = this.compteRepository.findByNom(nom);

		if (!comptes.isEmpty()) {
			log.info("Comptes trouvés pour le nom : {}", nom);
			return comptes;
		}
		log.warn("Aucun compte trouvé pour le nom : {}", nom);
		return new ArrayList<>();
	}

}
