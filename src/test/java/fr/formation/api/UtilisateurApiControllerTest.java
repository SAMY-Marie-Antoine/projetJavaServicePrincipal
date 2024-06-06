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
import fr.formation.request.UtilisateurRequest;

@ExtendWith(MockitoExtension.class)
class UtilisateurApiControllerTest {

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
    }

    /**
     * Test de la méthode findAll pour vérifier le statut de la réponse.
     * Vérifie que l'appel renvoie un statut 200 (OK).
     */
    @Test
    void shouldFindAllStatusOk() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test de la méthode findAll pour vérifier l'appel au repository.
     * Vérifie que la méthode findAll du repository est appelée et que la réponse contient la bonne taille de la liste.
     */
    @Test
    void shouldFindAllCallsRepository() throws Exception {
        // given
        Mockito.when(this.repository.findAll()).thenReturn(List.of(new Utilisateur(), new Utilisateur(), new Utilisateur()));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        result.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

        Mockito.verify(this.repository).findAll();
    }

    /**
     * Test de la méthode create pour vérifier le statut de la réponse.
     * Vérifie que la création d'un utilisateur renvoie un statut 201 (Created).
     */
    @Test
    void shouldCreateStatusCreated() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        UtilisateurRequest request = new UtilisateurRequest();

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

    /**
     * Test paramétré pour vérifier les mauvaises requêtes lors de la création d'un utilisateur.
     * Vérifie que les demandes avec des données invalides renvoient un statut 400 (Bad Request).
     */
    @ParameterizedTest
    @MethodSource("provideCreateUtilisateurRequests")
    void shouldCreateStatusBadRequest(String nom, LocalDate dateDeNaissance, String email, String motDePasse) throws Exception {
        // given
        UtilisateurRequest request = new UtilisateurRequest();

        request.setNom(nom);
        request.setDateDeNaissance(dateDeNaissance);
        request.setEmail(email);
        request.setMotDePasse(motDePasse);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT)
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
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

    /**
     * Test de la méthode getNameById pour vérifier la récupération du nom par ID.
     * Vérifie que le nom de l'utilisateur est correctement retourné.
     */
    @Test
    void shouldReturnNameById() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setNom("Test User");

        Mockito.when(this.repository.findById("1")).thenReturn(Optional.of(utilisateur));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1/name"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.content().string("Test User"));
    }
}
