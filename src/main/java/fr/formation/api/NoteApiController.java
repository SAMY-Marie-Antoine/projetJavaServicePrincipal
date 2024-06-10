package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


import fr.formation.model.Note;
import fr.formation.repository.NoteRepository;
import fr.formation.request.CreateNoteRequest;
import fr.formation.response.NoteResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/note")
@CrossOrigin("*")
public class NoteApiController {

	private static final Logger log = LoggerFactory.getLogger(UtilisateurApiController.class);

	@Autowired
	private NoteRepository noteRepository;

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

			/*Integer note = this.commentaireFeignClient.getNoteByProduitId(compte.getId());

            if (note != null) {
            	compteResponse.setNote(note);
            }*/
		}

		log.info("La méthode findAll a été exécutée avec succès");
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
	//public String update(@Valid @PathVariable("id") String id,@RequestBody CreateNoteRequest request) {
		
		Optional<Note> optionalNote = this.noteRepository.findById(id);
    if (!optionalNote.isPresent()) {
        log.warn("Note not found with id: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found with id: " + id);
    }

    Note notebdd = optionalNote.get();
    BeanUtils.copyProperties(request, notebdd);

    this.noteRepository.save(notebdd);

    log.info("Note mise à jour avec l'id : {}", notebdd.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(notebdd.getId());

		/*log.info("Mise à jour de la note avec l'id : {}", id);
		
		Note notebdd=this.noteRepository.findById(id).get();
		Note note = new Note();
		BeanUtils.copyProperties(request, notebdd);

		this.noteRepository.save(notebdd);

		log.info("Note mise à jour avec l'id : {}", notebdd.getId());
		return note.getId();*/
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String delete(@Valid @PathVariable("id") String id) {
		
		log.info("Suppression de la note avec l'id : {}", id);
		Optional<Note> notebdd = this.noteRepository.findById(id);

		if (notebdd.isEmpty()) {
			log.error("Note non trouvée avec l'id : {}", id);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Note inexistant");
		}

		this.noteRepository.deleteById(id);
		log.info("Note supprimée avec l'id : {}", id);
		return id;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@Valid @RequestBody CreateNoteRequest request) {
		
		log.info("Création d'une nouvelle note");
		Note note = new Note();
		BeanUtils.copyProperties(request, note);

		this.noteRepository.save(note);

		log.info("Note créée avec l'id : {}", note.getId());
		return note.getId();
	}

	//lister les notes d'un utilisateur spécifique
    @GetMapping("/user/{userId}")
    public List<NoteResponse> findByUserId(@Valid @PathVariable String userId) {
		
		log.info("Récupération des notes pour l'utilisateur avec l'id : {}", userId);

        List<Note> notes = this.noteRepository.findByUtilisateurId(userId);
        List<NoteResponse> response = new ArrayList<>();

        for (Note note : notes) {
            NoteResponse noteResponse = new NoteResponse();
            BeanUtils.copyProperties(note, noteResponse);
            response.add(noteResponse);
        }

		log.info("Renvoi de {} notes pour l'utilisateur avec l'id : {}", response.size(), userId);
        return response;
    }
}
