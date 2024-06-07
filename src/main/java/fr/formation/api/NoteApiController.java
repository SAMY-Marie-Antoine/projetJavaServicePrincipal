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

import fr.formation.model.Note;
import fr.formation.repository.NoteRepository;
import fr.formation.request.NoteRequest;
import fr.formation.response.NoteResponse;


@RestController
@RequestMapping("/api/note")
@CrossOrigin("*")
public class NoteApiController {
	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	//private VerificationFeignClient commentaireFeignClient;

	@GetMapping
	public List<NoteResponse> findAll() {
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

		return response;
	}



	@GetMapping("/{id}/name")
	public String getNameById(@PathVariable String id) {
		Optional<Note> optNote = this.noteRepository.findById(id);

		if (optNote.isPresent()) {
			return optNote.get().getNom();
		}

		return "- note not found -";
	}



	@GetMapping("/{id}")
	public Note findById(@PathVariable("id") String id) {
		Optional<Note> note = this.noteRepository.findById(id);

		if (note.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Id Note inexistant");
		}

		return note.get();
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String update(@PathVariable("id") String id,@RequestBody NoteRequest request) {
		Note notebdd=this.noteRepository.findById(id).get();
		Note note = new Note();
		BeanUtils.copyProperties(request, notebdd);

		this.noteRepository.save(notebdd);

		return note.getId();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public String delete(@PathVariable("id") String id,@RequestBody NoteRequest request) {
		Optional<Note> notebdd=this.noteRepository.findById(id);
		Note note = new Note();
		BeanUtils.copyProperties(request, notebdd);

		this.noteRepository.deleteById(id);

		return note.getId();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String create(@RequestBody NoteRequest request) {
		Note note = new Note();

		BeanUtils.copyProperties(request, note);

		this.noteRepository.save(note);

		return note.getId();
	}
}
