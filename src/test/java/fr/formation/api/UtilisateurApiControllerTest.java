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

    // Teste la méthode findAll pour vérifier que le statut HTTP est 200 (OK)
    @Test
    public void shouldFindAllStatusOk() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Teste la méthode findAll pour vérifier qu'elle appelle bien le repository et retourne les utilisateurs
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

    // Teste la méthode inscription pour vérifier que le statut HTTP est 201 (Created)
    @Test
    public void shouldCreateStatusCreated() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        
        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();

        request.setNom("Hedieh");
        request.setDateDeNaissance(LocalDate.of(2000, 1, 11));
        request.setEmail("test@example.com");
        request.setMotDePasse("password");
        request.setConfirmMotDePasse("password");

        Mockito.when(this.repository.save(Mockito.any())).thenReturn(utilisateur);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT +"/inscription" )
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").hasJsonPath());

        Mockito.verify(this.repository).save(Mockito.any());
    }

    // Teste la méthode inscription pour vérifier qu'elle retourne un statut HTTP 401 (Unauthorized) si les mots de passe ne correspondent pas
    @Test
    public void shouldReturnUnauthorizedWhenPasswordsDoNotMatch() throws Exception {
        // given - préparer les données de test
        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();
        request.setNom("Hedieh");
        request.setDateDeNaissance(LocalDate.of(2000, 1, 11));
        request.setEmail("test@example.com");
        request.setMotDePasse("password");
        request.setConfirmMotDePasse("differentPassword"); // mots de passe ne correspondent pas

        // when - effectuer une requête POST sur l'endpoint /inscription
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT + "/inscription")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then - vérifier que le statut HTTP est 401 (Unauthorized)
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Teste la méthode inscription pour vérifier qu'elle retourne un statut HTTP 400 (Bad Request) pour les demandes invalides
    @ParameterizedTest
    @MethodSource("provideCreateUtilisateurRequests")
    public void shouldCreateStatusBadRequest(String nom, LocalDate dateDeNaissance, String email, String motDePasse) throws Exception {
        // given
        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();
        request.setNom(nom);
        request.setDateDeNaissance(dateDeNaissance);
        request.setEmail(email);
        request.setMotDePasse(motDePasse);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT + "/inscription")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Fournit des données de test pour le test paramétré shouldCreateStatusBadRequest
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

    // Teste la méthode getNameById pour vérifier qu'elle retourne le nom de l'utilisateur pour un id donné
    @Test
    public void shouldReturnNameById() throws Exception {
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

    // Teste la méthode findById pour vérifier qu'elle retourne les informations de l'utilisateur pour un id donné    
    @Test
    public void shouldFindById() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setNom("Test User");

        Mockito.when(this.repository.findById("1")).thenReturn(Optional.of(utilisateur));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.nom").value("Test User"));
    }

    // Teste la méthode findById pour vérifier qu'elle retourne un statut HTTP 404 (Not Found) si l'utilisateur n'existe pas
    @Test
    public void shouldNotFindById() throws Exception {
        // given - préparer les données de test
        Mockito.when(this.repository.findById("1")).thenReturn(Optional.empty());

        // when - effectuer une requête GET sur l'endpoint /{id}
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1"));

        // then - vérifier que le statut HTTP est 404 (Not Found)
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    // Teste la méthode update pour vérifier qu'elle met à jour les informations de l'utilisateur
    @Test
    public void shouldUpdate() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setNom("Test User");

        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();
        request.setNom("Updated User");

        Mockito.when(this.repository.findById("1")).thenReturn(Optional.of(utilisateur));
        Mockito.when(this.repository.save(Mockito.any())).thenReturn(utilisateur);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .put(ENDPOINT + "/1")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(this.repository).save(Mockito.any());
    }

    // Teste la méthode delete pour vérifier qu'elle supprime un utilisateur par id
    @Test
    public void shouldDelete() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("1");
        utilisateur.setNom("Test User");

        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();

        Mockito.when(this.repository.findById("1")).thenReturn(Optional.of(utilisateur));

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .delete(ENDPOINT + "/1")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(this.repository).deleteById("1");
    }

    // Teste la méthode connexion pour vérifier qu'elle connecte un utilisateur avec email et mot de passe valides
    @Test
    public void shouldConnexion() throws Exception {
        // given
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");
        utilisateur.setMotDePasse("password");

        LoginUtilisateurRequest request = new LoginUtilisateurRequest();
        request.setEmail("test@example.com");
        request.setMotDePasse("password");

        Mockito.when(this.repository.findByEmailAndMotDePasse("test@example.com", "password"))
               .thenReturn(Optional.of(utilisateur));

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT + "/connexion")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
        Mockito.verify(this.repository).findByEmailAndMotDePasse("test@example.com", "password");
    }

    // Teste la méthode inscription pour vérifier qu'elle inscrit un nouvel utilisateur
    @Test
    public void shouldInscription() throws Exception {
        // given
        InscriptionUtilisateurRequest request = new InscriptionUtilisateurRequest();
        request.setEmail("test@example.com");
        request.setMotDePasse("password");
        request.setConfirmMotDePasse("password");
        request.setNom("Test User");
        request.setDateDeNaissance(LocalDate.of(2000, 1, 1));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("test@example.com");
        utilisateur.setMotDePasse("password");
        utilisateur.setNom("Test User");
        utilisateur.setDateDeNaissance(LocalDate.of(2000, 1, 1));

        // simulate that no existing user is found with the given email and password
        Mockito.when(this.repository.findByEmailAndMotDePasse(request.getEmail(), request.getMotDePasse()))
        .thenReturn(Optional.empty());

        Mockito.when(this.repository.save(Mockito.any())).thenReturn(utilisateur);

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT + "/inscription")
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated());
        Mockito.verify(this.repository).save(Mockito.any());
    }
}
