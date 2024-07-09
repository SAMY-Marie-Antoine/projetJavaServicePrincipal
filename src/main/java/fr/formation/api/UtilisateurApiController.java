package fr.formation.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
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

import fr.formation.event.VerificationCreatedEventUtilisateur;
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
import fr.formation.response.ResponseMessage;
import fr.formation.service.MotDePasseUtilisateurService;
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
	private MotDePasseUtilisateurService motDePasseUtilisateurService;

	@Autowired
	private StreamBridge streamBridge;

	VerificationCreatedEventUtilisateur event = new VerificationCreatedEventUtilisateur();

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

			event.setMessage("Vérification l'utilisateur: présent");
			event.setLevel(optUtilisateur.get().getNom());
			event.setPassword(optUtilisateur.get().getMotDePasse());
			event.setUtilisateurId(optUtilisateur.get().getId());
			event.setTimestamp(LocalDateTime.now());
			
			log.debug("Utilisateur ID trouvé ->", optUtilisateur.get().getId(), (this.streamBridge.send("verification.created",event)));
			return optUtilisateur.get().getNom();
		}
		else {
			event.setMessage("Vérification l'utilisateur: absent");
			event.setLevel(optUtilisateur.get().getNom());
			event.setPassword(optUtilisateur.get().getMotDePasse());
			event.setUtilisateurId(optUtilisateur.get().getId());
			event.setTimestamp(LocalDateTime.now());
			
			log.debug("Utilisateur ID non trouvé ->", optUtilisateur.get().getId(), (this.streamBridge.send("verification.rejected",event)));

		}

		log.warn("Utilisateur non trouvé dans la méthode getNameById avec l'id: " + id);
		return "- Utilisateur non trouvé -";
	}

	@GetMapping("/{id}")
	public Utilisateur findById(@Valid @PathVariable("id") String id) {

		log.info("Exécution de la méthode findById avec l'id: " + id);
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findById(id);

		if (optUtilisateur.isEmpty()) {
			log.warn("Utilisateur non trouvé dans la méthode findById avec l'id: " + id);

			event.setMessage("Vérification l'utilisateur: absent");
			event.setLevel(optUtilisateur.get().getNom());
			event.setPassword(optUtilisateur.get().getMotDePasse());
			event.setUtilisateurId(optUtilisateur.get().getId());
			event.setTimestamp(LocalDateTime.now());
			
			log.debug("Utilisateur ID ->", optUtilisateur.get().getId(), (this.streamBridge.send("verification.rejected",event)));

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Utilisateur inexistant");
		}
		else {
			event.setMessage("Vérification l'utilisateur: présent");
			event.setLevel(optUtilisateur.get().getNom());
			event.setPassword(optUtilisateur.get().getMotDePasse());
			event.setUtilisateurId(optUtilisateur.get().getId());
			event.setTimestamp(LocalDateTime.now());
			
			log.debug("Utilisateur ID ->", optUtilisateur.get().getId(), (this.streamBridge.send("verification.created",event)));

		}

		log.info("La méthode findById a été exécutée avec succès");
		return optUtilisateur.get();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String update(@Valid @PathVariable("id") String id,@RequestBody InscriptionUtilisateurRequest request) {

		log.info("Exécution de la méthode update avec l'id: " + id);

		Utilisateur utilisateurbdd = this.utilisateurRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Utilisateur inexistant"));


		BeanUtils.copyProperties(request, utilisateurbdd);

		if(utilisateurbdd.getId().isEmpty()) {
			event.setMessage("Modification utilisateur : non modifié");
			event.setLevel(utilisateurbdd.getNom());
			event.setPassword(utilisateurbdd.getMotDePasse());
			event.setUtilisateurId(utilisateurbdd.getId());
			event.setTimestamp(LocalDateTime.now());
		

			log.debug("Exécution de la méthode update avec l'id: ", utilisateurbdd.getId(), (this.streamBridge.send("verification.rejected",event)));
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Echec Modification utilisateur");

		}
		else {
			event.setMessage("Modification utilisateur : modifié");
			event.setLevel(utilisateurbdd.getNom());
			event.setPassword(utilisateurbdd.getMotDePasse());
			event.setUtilisateurId(utilisateurbdd.getId());
			event.setTimestamp(LocalDateTime.now());
		
			log.debug("Exécution de la méthode update avec l'id: ", utilisateurbdd.getId(), (this.streamBridge.send("verification.created",event)));
			this.utilisateurRepository.save(utilisateurbdd);
		}
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

		if(utilisateurbdd.get().getId().isEmpty()) {
			event.setMessage("Suppression utilisateur : non supprimé");
			event.setLevel(utilisateurbdd.get().getNom());
			event.setPassword(utilisateurbdd.get().getMotDePasse());
			event.setUtilisateurId(utilisateurbdd.get().getId());
			event.setTimestamp(LocalDateTime.now());
		

			log.debug("Exécution de la méthode delete avec l'id: ", utilisateurbdd.get().getId(), (this.streamBridge.send("verification.rejected",event)));
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Echec Suppression utilisateur");

		}
		else {
			event.setMessage("Suppression utilisateur : supprimé");
			event.setLevel(utilisateurbdd.get().getNom());
			event.setPassword(utilisateurbdd.get().getMotDePasse());
			event.setUtilisateurId(utilisateurbdd.get().getId());
			event.setTimestamp(LocalDateTime.now());
		
			log.debug("Exécution de la méthode delete avec l'id: ", utilisateurbdd.get().getId(), (this.streamBridge.send("verification.created",event)));
			this.utilisateurRepository.deleteById(id);

		}

		log.info("La méthode delete a été exécutée avec succès");
		return utilisateur.getId();
	}


	@PostMapping("/connexion")
	public Utilisateur connexion(@Valid @RequestBody LoginUtilisateurRequest request) {
		
		log.info("Exécution de la méthode connexion");
		
		// Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndMotDePasse(request.getEmail(), request.getMotDePasse());
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmail(request.getEmail());
		
		if(optUtilisateur.isEmpty()) {
			log.warn("Utilisateur non trouvé dans la méthode connexion");
			event.setMessage("Echec connexion utilisateur : Utilisateur n'existe pas");
			event.setLevel(request.getEmail());
			event.setPassword(request.getMotDePasse());
			event.setTimestamp(LocalDateTime.now());
						
			log.debug("Utilisateur n'existe pas dans la méthode connexion:", request.getEmail(), (this.streamBridge.send("verification.rejected",event)));


			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	
		if(!optUtilisateur.get().getEmail().equals(request.getEmail()) ) {

			log.warn("Email inexistant dans la méthode connexion");
			event.setMessage("Echec inexistant : Email n'existe pas");
			event.setLevel(request.getEmail());
			event.setTimestamp(LocalDateTime.now());

			System.out.println("email inexistant !" + optUtilisateur.get().getEmail());
			log.debug("Email n'existe pas dans la méthode connexion:", request.getEmail(), (this.streamBridge.send("verification.rejected",event)));


			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		//ajout H 08/07	
		Utilisateur utilisateur = optUtilisateur.get();
		if (!motDePasseUtilisateurService.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
		// if(!optUtilisateur.get().getMotDePasse().equals(request.getMotDePasse())) {
			
			log.warn("Mot de passe incorrect dans la méthode connexion");
			System.out.println("mot de passe incorrect");
			event.setMessage("Mot de passe: Mot de passe incorrect");
			event.setLevel(request.getEmail());
			event.setTimestamp(LocalDateTime.now());

			System.out.println("Mot de passe incorrect !" + optUtilisateur.get().getEmail());
			log.debug("Mot de passe incorrect dans la méthode connexion:", request.getMotDePasse(), (this.streamBridge.send("verification.rejected",event)));


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
			event.setMessage("Echec Inscription utilisateur : Email déjà existant");
			event.setLevel(request.getEmail());
			event.setPassword(request.getMotDePasse());
			event.setTimestamp(LocalDateTime.now());
						
			log.debug("Email déjà existant dans la méthode inscription:", request.getEmail(), (this.streamBridge.send("verification.rejected",event)));

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email déjà existant");
		}

		if (!request.getMotDePasse().equals(request.getConfirmMotDePasse())) {
			log.warn("La confirmation du mot de passe ne correspond pas dans la méthode inscription");
			event.setMessage("Echec Inscription utilisateur : La confirmation du mot de passe ne correspond pas");
			event.setLevel(request.getNom());
			event.setPassword(request.getMotDePasse());
			event.setTimestamp(LocalDateTime.now());
						

			log.debug("La confirmation du mot de passe ne correspond pas dans la méthode inscription:", optUtilisateur.get().getId(), (this.streamBridge.send("verification.rejected",event)));

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La confirmation du mot de passe ne correspond pas");
		}

		// Utilisation de Feign pour vérifier la vulnérabilité du mot de passe
		boolean motDePasseCompromis=this.verificationFeignClient.getMotDePasseCompromis(request.getMotDePasse());
		if (motDePasseCompromis ) {
			log.warn("Le mot de passe est compromis dans la méthode inscription");
			event.setMessage("Mot de passe est compromis : oui");
			event.setLevel(request.getNom());
			event.setPassword(request.getMotDePasse());
			event.setTimestamp(LocalDateTime.now());
			

			log.debug("Le mot de passe est compromis dans la méthode inscription:", request.getMotDePasse(), (this.streamBridge.send("verification.rejected",event)));

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est compromis");
		}

		// Utilisation de Feign pour vérifier la force du mot de passe
		boolean motDePasseForce=this.verificationFeignClient.getForceMotDePasse(request.getMotDePasse());
		if (!motDePasseForce ) {
			log.warn("Le mot de passe est faible dans la méthode inscription");
			
			event.setMessage("Mot de passe est faible : oui");
			event.setLevel(request.getNom());
			event.setPassword(request.getMotDePasse());
			event.setTimestamp(LocalDateTime.now());
					
			log.debug("Le mot de passe est faible dans la méthode inscription:", request.getMotDePasse(), (this.streamBridge.send("verification.rejected",event)));
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Le mot de passe est faible");

		}

		Utilisateur utilisateur = new Utilisateur();
		BeanUtils.copyProperties(request, utilisateur);

		// Crypter le mot de passe avant de l'enregistrer
		utilisateur.setMotDePasse(motDePasseUtilisateurService.crypterMotDePasse(request.getMotDePasse()));
		this.utilisateurRepository.save(utilisateur);

		event.setMessage("Inscription utilisateur : inscrit");
		event.setTimestamp(LocalDateTime.now());
		
		log.debug("Utilisateur inscrit: inscrit", utilisateur.getId(), (this.streamBridge.send("verification.created",event)));

		log.info("La méthode inscription a été exécutée avec succès");
		return new InscriptionUtilisateurResponse(utilisateur.getId());
	}



	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
		log.info("Début de la méthode forgotPassword");

		// Récupérer l'email à partir du corps de la requête
		String email = body.get("email");
		if (email == null || email.isEmpty()) {
			log.error("L'email est obligatoire");
			return ResponseEntity.badRequest().body("L'email est obligatoire");
		}

		// Rechercher l'utilisateur par email
		Optional<Utilisateur> optUtilisateur = utilisateurRepository.findByEmail(email);
		if (!optUtilisateur.isPresent()) {
			log.error("Aucun utilisateur trouvé avec cet email : " + email);
			return ResponseEntity.badRequest().body("Aucun utilisateur trouvé avec cet email");
		}

		// Générer un token de réinitialisation du mot de passe
		String resetToken = UUID.randomUUID().toString();
		log.info("Token de réinitialisation du mot de passe généré : " + resetToken);

		Utilisateur utilisateur = optUtilisateur.get();

		// Enregistrer le token de réinitialisation du mot de passe dans la base de données
		utilisateur.setResetToken(resetToken);
		utilisateurRepository.save(utilisateur);
		log.info("Token de réinitialisation du mot de passe enregistré pour l'utilisateur : " + email);

		// Envoyer le lien de réinitialisation du mot de passe par email
		// Vous devrez implémenter cette méthode selon votre service d'envoi d'email
		motDePasseUtilisateurService.sendPasswordResetLink(email, resetToken);
		log.info("Lien de réinitialisation du mot de passe envoyé à l'utilisateur : " + email);

		log.info("Fin de la méthode forgotPassword");
		return ResponseEntity.ok("Un lien de réinitialisation du mot de passe a été envoyé à votre email");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
		log.info("Début de la méthode resetPassword");

		// Récupérer le token et le nouveau mot de passe à partir du corps de la requête
		String resetToken = body.get("resetToken");
		String newPassword = body.get("newPassword");
		if (resetToken == null || resetToken.isEmpty() || newPassword == null || newPassword.isEmpty()) {
			log.error("Le token et le nouveau mot de passe sont obligatoires");
			return ResponseEntity.badRequest().body("Le token et le nouveau mot de passe sont obligatoires");
		}

		// Rechercher l'utilisateur par le token de réinitialisation du mot de passe
		Optional<Utilisateur> optUtilisateur = utilisateurRepository.findByResetToken(resetToken);
		if (!optUtilisateur.isPresent()) {
			log.error("Aucun utilisateur trouvé avec ce token de réinitialisation du mot de passe : " + resetToken);
			return ResponseEntity.badRequest().body("Aucun utilisateur trouvé avec ce token de réinitialisation du mot de passe");
		}

		// Utilisation de Feign pour vérifier la force du mot de passe
		boolean motDePasseForce = this.verificationFeignClient.getForceMotDePasse(newPassword);
		if (!motDePasseForce ) {
			log.warn("Le nouveau mot de passe est faible");
			return ResponseEntity.badRequest().body("Le nouveau mot de passe est faible");
		}

		Utilisateur utilisateur = optUtilisateur.get();

		// Crypter le nouveau mot de passe avant de l'enregistrer
		utilisateur.setMotDePasse(motDePasseUtilisateurService.crypterMotDePasse(newPassword));
		// Réinitialiser le token de réinitialisation du mot de passe
		utilisateur.setResetToken(null);
		utilisateurRepository.save(utilisateur);
		log.info("Le mot de passe a été réinitialisé avec succès pour l'utilisateur : " + utilisateur.getEmail());

		log.info("Fin de la méthode resetPassword");
		//return ResponseEntity.ok("Votre mot de passe a été réinitialisé avec succès");
		// Au lieu de simplement renvoyer une chaîne, essayez de renvoyer un objet JSON.
		ResponseMessage responseMessage = new ResponseMessage("Votre mot de passe a été réinitialisé avec succès");
    	return ResponseEntity.ok(responseMessage);
	} 

}
