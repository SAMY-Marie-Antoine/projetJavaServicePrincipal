package fr.formation.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.formation.model.Note;
import fr.formation.repository.NoteRepository;
import fr.formation.request.CreateNoteRequest;
import jakarta.ws.rs.core.MediaType;

@ExtendWith(MockitoExtension.class)
public class NoteApiControllerTest {
    
    private final static String ENDPOINT = "/api/note";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteApiController ctrl;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.ctrl).build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    // Teste la méthode findAll pour vérifier que le statut HTTP est 200 (OK)
    @Test
    public void shouldFindAllStatusOk() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Teste la méthode findById pour vérifier que la note est bien trouvée
    @Test
    public void shouldFindByIdStatusOk() throws Exception {
        // given
        String noteId = "1";
        Note note = new Note();
        note.setId(noteId);
        Mockito.when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + noteId));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(noteId));
    
        Mockito.verify(this.noteRepository).findById(noteId);

    }

    // Teste la méthode findById pour vérifier que la réponse est 404 quand la note n'est pas trouvée
    @Test
    public void shouldReturn404WhenNoteNotFound() throws Exception {
        // given
        String noteId = "1";
        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + noteId));

        // then
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Teste la méthode create pour vérifier que la note est bien créée
    @Test
    public void shouldCreateNote() throws Exception {
        // given
        CreateNoteRequest request = new CreateNoteRequest();
        request.setNom("Test Note");
        request.setDescription("Description de test");
        request.setDateAjout(LocalDateTime.now());
        
        /*Note note = new Note();
        note.setId("1");
        when(noteRepository.save(any(Note.class))).thenReturn(note);
        */
        String requestJson = this.mapper.writeValueAsString(request);

        // when
        /*ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));
*/
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
            .contentType("application/json")
            .content(requestJson));
            
        // then
        /*result.andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.jsonPath("$").value(note.getId()));
    */
    result.andExpect(MockMvcResultMatchers.status().isCreated());

    }

    // Teste la méthode update pour vérifier que la note est bien mise à jour
    @Test
    public void shouldUpdateNote() throws Exception {
        // given
        String noteId = "1";
        CreateNoteRequest request = new CreateNoteRequest();
        request.setNom("Updated Note");
        request.setDescription("Description mise à jour");
        request.setDateAjout(LocalDateTime.now());
        
        Note note = new Note();
        note.setId(noteId);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.put(ENDPOINT + "/" + noteId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)));

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.jsonPath("$").value(noteId));
    }

    // Teste la méthode delete pour vérifier que la note est bien supprimée
    @Test
    public void shouldDeleteNote() throws Exception {
        // given
        String noteId = "1";
        Note note = new Note();
        note.setId(noteId);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT + "/" + noteId));

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.jsonPath("$").value(noteId));
    }

    // Teste la méthode findByUserId pour vérifier que les notes d'un utilisateur sont bien récupérées
    @Test
    public void shouldFindByUserId() throws Exception {
        // given
        String userId = "1";
        List<Note> notes = new ArrayList<>();
        Note note = new Note();
        note.setId("1");
        notes.add(note);
        when(noteRepository.findByUtilisateurId(userId)).thenReturn(notes);

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/user/" + userId));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(note.getId()));
    }

    // Teste la méthode getNameById pour vérifier que le nom de la note est bien récupéré
    @Test
    public void shouldGetNameById() throws Exception {
        // given
        String noteId = "1";
        String noteName = "Test Note";
        Note note = new Note();
        note.setId(noteId);
        note.setNom(noteName);
        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + noteId + "/name"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string(noteName));
    }

}
