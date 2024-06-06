package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import fr.formation.model.Utilisateur;
import fr.formation.repository.UtilisateurRepository;
import fr.formation.request.UtilisateurRequest;
import fr.formation.response.UtilisateurResponse;

@RestController
@RequestMapping("/api/utilisateur")
@CrossOrigin("*")
public class UtilisateurApiController {
	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@Autowired
	//private CommentaireFeignClient commentaireFeignClient;

	@GetMapping
	public List<UtilisateurResponse> findAll() {
		List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
		List<UtilisateurResponse> response = new ArrayList<>();

		for (Utilisateur utilisateur : utilisateurs) {
			UtilisateurResponse compteResponse = new UtilisateurResponse();

			BeanUtils.copyProperties(utilisateur, compteResponse);

			response.add(compteResponse);

			/*Integer note = this.commentaireFeignClient.getNoteByProduitId(compte.getId());

            if (note != null) {
            	compteResponse.setNote(note);
            }*/
		}

		return response;
	}



	@GetMapping("/{id}/name")
	public String getNameById(@PathVariable String id) {
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findById(id);

		if (optUtilisateur.isPresent()) {
			return optUtilisateur.get().getNom();
		}

		return "- user not found -";
	}

	@GetMapping("/{id}")
	public Utilisateur findById(@PathVariable("id") String id) {
		Optional<Utilisateur> utilisateur = this.utilisateurRepository.findById(id);

		if (utilisateur.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Utilisateur inexistant");
		}

		return utilisateur.get();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String update(@PathVariable("id") String id,@RequestBody UtilisateurRequest request) {
		Utilisateur utilisateurbdd=this.utilisateurRepository.findById(id).get();
		Utilisateur utilisateur = new Utilisateur();
		BeanUtils.copyProperties(request, utilisateurbdd);

		this.utilisateurRepository.save(utilisateurbdd);

		return utilisateur.getId();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String delete(@PathVariable("id") String id,@RequestBody UtilisateurRequest request) {
		Optional<Utilisateur> utilisateurbdd=this.utilisateurRepository.findById(id);
		Utilisateur utilisateur = new Utilisateur();
		BeanUtils.copyProperties(request, utilisateurbdd);

		this.utilisateurRepository.deleteById(id);

		return utilisateur.getId();
	}



	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@RequestBody UtilisateurRequest request) {
		Utilisateur utilisateur = new Utilisateur();

		BeanUtils.copyProperties(request, utilisateur);

		this.utilisateurRepository.save(utilisateur);

		return utilisateur.getId();
	}

	@PostMapping("/connexion")
	public Utilisateur connexion(@RequestBody UtilisateurRequest request) {
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndMotDePasse(request.getEmail(), request.getMotDePasse());

		if(optUtilisateur.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		if(optUtilisateur.get().getEmail() != request.getEmail()) {


			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		
		if(optUtilisateur.get().getMotDePasse() != request.getMotDePasse()) {

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		
		return optUtilisateur.get();
	}

	@PostMapping("/inscription")
	public Utilisateur inscription(@RequestBody UtilisateurRequest request) {
		Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findByEmailAndMotDePasse(request.getEmail(), request.getMotDePasse());


		if(optUtilisateur.get().getEmail()== request.getEmail()) {

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		if(request.getMotDePasse() != request.getConfirmMotDePasse()) {

			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		Utilisateur utilisateur = new Utilisateur();

		BeanUtils.copyProperties(request, utilisateur); // copie les propriétés de même type et nom depuis inscriptionDTO vers utilisateur

		utilisateur.setEmail(request.getEmail());

		utilisateur = this.utilisateurRepository.save(utilisateur);

		return utilisateur;
	}
}
