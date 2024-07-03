package fr.formation.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import fr.formation.event.NoteEvent;
import fr.formation.model.Compte;
import fr.formation.model.Note;
import fr.formation.model.Utilisateur;
import fr.formation.repository.NoteRepository;
import fr.formation.repository.UtilisateurRepository;
import fr.formation.request.CreateNoteRequest;
import fr.formation.response.NoteResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/note")
@CrossOrigin("*")
public class NoteApiController {

	private static final Logger log = LoggerFactory.getLogger(NoteApiController.class);

	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@Autowired
	private StreamBridge streamBridge;

	@Autowired
	//private VerificationFeignClient commentaireFeignClient;

	public NoteApiController(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
		log.info("Initialisation de NoteApiController");

	}

	@GetMapping
	public List<NoteResponse> findAll() {

		log.info("Récupération de toutes les notes");

		List<Note> notes = this.noteRepository.findAll();
		List<NoteResponse> response = new ArrayList<>();

		for (Note note : notes) {
			NoteResponse noteResponse = new NoteResponse();
			BeanUtils.copyProperties(note, noteResponse);
			response.add(noteResponse);
		}

		log.info("Liste de tous les notes récupérée avec succès");
		log.info("Renvoi de {} notes", response.size());
		return response;
	}

	@GetMapping("/{id}/name")
	public String getNameById(@Valid @PathVariable String id) {

		log.info("Récupération du nom de la note avec l'id : {}", id);
		Optional<Note> optNote = this.noteRepository.findById(id);

		if (optNote.isPresent()) {
			log.info("Note trouvée avec le nom : {}", optNote.get().getNom());
			return optNote.get().getNom();
		}
		log.warn("Note non trouvée avec l'id : {}", id);
		return "- note not found -";
	}

	@GetMapping("/{id}")
	public Note findById(@Valid @PathVariable("id") String id) {
		log.info("Récupération de la note avec l'id : {}", id);

		Optional<Note> note = this.noteRepository.findById(id);

		if (note.isEmpty()) {
			log.error("Note non trouvée avec l'id : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Note inexistant");
		}
		log.info("Note trouvée : {}", note.get());
		return note.get();
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> update(@Valid @PathVariable("id") String id, @RequestBody CreateNoteRequest request) {
		
		Optional<Note> optionalNote = this.noteRepository.findById(id);
		if (!optionalNote.isPresent()) {
			// Si le note n'existe pas, créer un nouvel événement de journalisation
			NoteEvent event = new NoteEvent();
			event.setLevel("ERROR");
			event.setMessage("La note avec l'ID : " + id + " n'existe pas.");
			event.setTimestamp(LocalDateTime.now());
			// Envoi de l'événement à l'écouteur approprié
			streamBridge.send("note.errorUpdated", event);
			log.error(event.getMessage());

			log.warn("Note not found with id: {}", id);
			
			// Retourner une chaîne d'erreur
			return new ResponseEntity<>(event.getMessage(), HttpStatus.NOT_FOUND);
		}
			// Création d'un nouvel objet NoteEvent pour la journalisation
			NoteEvent event1 = new NoteEvent();
			event1.setLevel("INFO");
			event1.setMessage("Mise à jour du note avec l'ID : " + id);
			event1.setTimestamp(LocalDateTime.now());
			// Envoi de l'événement à l'écouteur approprié
			streamBridge.send("note.updated", event1);

			log.info(event1.getMessage());

			Note notebdd = optionalNote.get();
			BeanUtils.copyProperties(request, notebdd);
			// mettre à jour la date de modification
			notebdd.setDateModif(LocalDateTime.now());

			this.noteRepository.save(notebdd);

			// Création d'un nouvel objet NoteEvent pour la journalisation
			NoteEvent event2 = new NoteEvent();
			event2.setLevel("INFO");
			event2.setMessage("Note mis à jour avec succès pour l'ID : " + id);
			event2.setTimestamp(LocalDateTime.now());
			// Envoi de l'événement à l'écouteur approprié
			streamBridge.send("note.updated", event2);
		
			log.info(event2.getMessage());
		
			log.info("Note mise à jour avec l'id : {}", notebdd.getId());

			// new ResponseEntity<>(notebdd.getId(), HttpStatus.OK) crée une nouvelle réponse 
			//avec le corps notebdd.getId() et le statut HTTP 200 (OK).
			return new ResponseEntity<>(notebdd.getId(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@Valid @PathVariable("id") String id) {
		
		NoteEvent event = new NoteEvent();

		// Définition du niveau de l'événement
		event.setLevel("INFO");
		// Définition du message de l'événement
		event.setMessage("Suppression du note avec l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement au StreamBridge
		streamBridge.send("note.deleted", event);
		log.info(event.getMessage());

		log.info("Suppression de la note avec l'id : {}", id);
		Optional<Note> notebdd = this.noteRepository.findById(id);

		if (notebdd.isEmpty()) {

			// Mise à jour du message de l'événement
			event.setMessage("Note non trouvé avec l'ID : " + id);
			event.setTimestamp(LocalDateTime.now());
			// Envoi du nouvel événement au StreamBridge
			streamBridge.send("note.deleted", event);
			log.warn(event.getMessage());

			log.warn("Note non trouvée avec l'id : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Note inexistant");
		}

		this.noteRepository.deleteById(id);

		// Mise à jour du message de l'événement
		event.setMessage("Note supprimé avec succès pour l'ID : " + id);
		event.setTimestamp(LocalDateTime.now());
		// Envoi du nouvel événement au StreamBridge
		streamBridge.send("note.deleted", event);
		log.info(event.getMessage());

		log.info("Note supprimée avec l'id : {}", id);
		
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@Valid @RequestBody CreateNoteRequest request) throws Exception{
		
		log.info("Création d'une nouvelle note");
		Note note = new Note();
		BeanUtils.copyProperties(request, note);

		note.setId(request.getUtilisateur().getId());//h 22h
		note.setDateAjout(LocalDateTime.now());
		note.setDateModif(LocalDateTime.now());

		// Sauvegarde de la note dans la base de données
		this.noteRepository.save(note);

		// Création d'un événement de note
		NoteEvent event = new NoteEvent();

		event.setLevel("INFO");
		event.setMessage("Note créée avec l'id : " + note.getId());
		event.setTimestamp(LocalDateTime.now());

		// Envoi de l'événement de note via StreamBridge
		streamBridge.send("note.created", event);

		log.info("Note créée avec l'id : {}", note.getId());
		return note.getId();
	}

	//lister les notes d'un utilisateur spécifique
    @GetMapping("/user/{userId}")
    public List<NoteResponse> findByUserId(@Valid @PathVariable String userId) {
		
		NoteEvent event = new NoteEvent();
		// Définition du niveau de l'événement
		event.setLevel("INFO");
		// Définition du message de l'événement
		event.setMessage("Recherche des notes pour l'utilisateur avec l'ID : " + userId);
		// Ajout de l'horodatage à l'événement
		event.setTimestamp(LocalDateTime.now());
		// Envoi de l'événement au StreamBridge
		streamBridge.send("note.userRetrieved", event);
		log.info(event.getMessage());

		log.info("Récupération des notes pour l'utilisateur avec l'id : {}", userId);

        List<Note> notes = this.noteRepository.findByUtilisateurId(userId);
        List<NoteResponse> response = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = new NoteResponse();
            BeanUtils.copyProperties(note, noteResponse);
            response.add(noteResponse);
        }

		// Mise à jour du message de l'événement
		event.setMessage("Notes de l'utilisateur avec l'ID : " + userId + " récupérés avec succès");
		event.setTimestamp(LocalDateTime.now());
		// Envoi du nouvel événement au StreamBridge
		streamBridge.send("note.userRetrieved", event);
		log.info(event.getMessage());

		log.info("Renvoi de {} notes pour l'utilisateur avec l'id : {}", response.size(), userId);
        return response;
    }

	@GetMapping("/by-name/{nom}")
	public List<Note> getCompteByName(@Valid @PathVariable String nom) {
		log.info("Recherche des notes avec le nom : {}", nom);
		List<Note> notes = this.noteRepository.findByNom(nom);

		if (!notes.isEmpty()) {
			log.info("Notes trouvées pour le nom : {}", nom);
			return notes;
		}
		log.warn("Aucun note trouvée pour le nom : {}", nom);
		return new ArrayList<>();
	}
}
