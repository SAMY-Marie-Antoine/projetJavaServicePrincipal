package fr.formation.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.formation.model.Utilisateur;
import fr.formation.repository.UtilisateurRepository;
import fr.formation.request.InscriptionUtilisateurRequest;
import fr.formation.request.LoginUtilisateurRequest;

@ExtendWith(MockitoExtension.class)
public class UtilisateurApiControllerTest {

    private final static String ENDPOINT = "/api/utilisateur";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private UtilisateurRepository repository;

    @InjectMocks
    private UtilisateurApiController ctrl;

    @BeforeEach
    public void beforeEach() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.ctrl).build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    public void shouldFindAllStatusOk() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldFindAllCallsRepository() throws Exception {
        // given
        Mockito.when(this.repository.findAll()).thenReturn(List.of(new Utilisateur(), new Utilisateur(), new Utilisateur()));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        result.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

        Mockito.verify(this.repository).findAll();
    }

    @Test
    public void shouldCreateStatusCreated() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        //utilisateur.setId("1"); // Ajout H
        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();

        request.setNom("Hedieh");
        request.setDateDeNaissance(LocalDate.of(2000, 1, 11));
        request.setEmail("test@example.com");
        request.setMotDePasse("password");

        Mockito.when(this.repository.save(Mockito.any())).thenReturn(utilisateur);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT)
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").hasJsonPath());

        Mockito.verify(this.repository).save(Mockito.any());
    }

    @ParameterizedTest
    @MethodSource("provideCreateUtilisateurRequests")
    public void shouldCreateStatusBadRequest(String nom, LocalDate dateDeNaissance, String email, String motDePasse) throws Exception {
        
    	InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();
        request.setNom(nom);
        request.setDateDeNaissance(dateDeNaissance);
        request.setEmail(email);
        request.setMotDePasse(motDePasse);

        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT)
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private static Stream<Arguments> provideCreateUtilisateurRequests() {
        return Stream.of(
            Arguments.of(null, LocalDate.of(2000, 1, 11), "test@example.com", "password"),
            Arguments.of(" ", LocalDate.of(2001, 2, 12), "test@example.com", "password"),
            Arguments.of("", LocalDate.of(2002, 3, 13), "test@example.com", "password"),
            Arguments.of("Un test", LocalDate.of(2000, 1, 11), null, "password"),
            Arguments.of("Un test", LocalDate.of(2001, 2, 12), " ", "password"),
            Arguments.of("Un test", LocalDate.of(2002, 3, 13), "", "password"),
            Arguments.of("Un test", null, "test@example.com", "password"),
            Arguments.of("Un test", LocalDate.of(2002, 1, 11), "test@example.com", null),
            Arguments.of("Un test", LocalDate.of(2001, 2, 12), "test@example.com", " ")
        );
    }

    @Test
    public void shouldReturnNameById() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setNom("Test User");

        Mockito.when(this.repository.findById("1")).thenReturn(Optional.of(utilisateur));

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1/name"));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string("Test User"));
    }
}
