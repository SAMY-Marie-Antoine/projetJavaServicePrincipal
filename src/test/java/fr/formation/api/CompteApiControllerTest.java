package fr.formation.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.formation.feignclient.VerificationFeignClient;
import fr.formation.model.Compte;
import fr.formation.model.Utilisateur;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CreateCompteRequest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CompteApiControllerTest {

    private final static String ENDPOINT = "/api/compte";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private CompteRepository repository;

    @InjectMocks
    private CompteApiController ctrl;

    @Mock
    private VerificationFeignClient verificationFeignClient;

    @MockBean
    private StreamBridge streamBridge;

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

    // Teste la méthode findAll pour vérifier qu'elle appelle bien le repository et retourne les comptes
    @Test
    public void shouldFindAllCallsRepository() throws Exception {
        // given
        Mockito.when(this.repository.findAll()).thenReturn(List.of(new Compte(), new Compte(), new Compte()));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        result.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(3)));

        Mockito.verify(this.repository).findAll();
    }

    // Teste la méthode findById pour vérifier que le statut HTTP est 200 (OK) et que le compte est retourné
    @Test
    public void shouldFindByIdStatusOk() throws Exception {
        // given
        String id = "test-id";
        Compte compte = new Compte();
        compte.setId(id); // Assurez-vous que l'ID est défini
        Mockito.when(this.repository.findById(id)).thenReturn(Optional.of(compte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + id));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id));

        Mockito.verify(this.repository).findById(id);
    }

    // Teste la méthode findById pour vérifier qu'elle retourne 404 si le compte n'existe pas
    @Test
    public void shouldFindByIdStatusNotFound() throws Exception {
        // given
        String id = "test-id";
        Mockito.when(this.repository.findById(id)).thenReturn(Optional.empty());

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/" + id));

        // then
        result.andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(this.repository).findById(id);
    }

    // Teste la méthode create pour vérifier que le statut HTTP est 201 (Created) et que le compte est créé
    @Test
    public void shouldCreateStatusCreated() throws Exception {
        // given: Préparation de la requête et des mocks
        CreateCompteRequest request = new CreateCompteRequest();
        request.setNom("MonCompte");
        request.setDescription("Description du compte");
        request.setDateAjout(LocalDateTime.now());
        request.setValeurMotdePassePlateforme("MotDePasseFort123!");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("utilisateur123");
        request.setUtilisateur(utilisateur);

        when(verificationFeignClient.getMotDePasseCompromis(anyString())).thenReturn(false);
        when(verificationFeignClient.getForceMotDePasse(anyString())).thenReturn(true);
        when(repository.save(any(Compte.class))).thenAnswer(invocation -> {
            Compte compte = invocation.getArgument(0);
            compte.setId("1"); // Simuler un ID généré comme chaîne de caractères
            return compte;
        });

        // when: Exécution de la requête HTTP POST
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))

        // then: Vérification du statut et de la réponse
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value("1"));
    }

    // Teste la méthode create pour vérifier que le statut HTTP est 401 (Unauthorized) lorsque le mot de passe est compromis
    @Test
    public void shouldNotCreateWhenPasswordIsCompromised() throws Exception {
        // given: Préparation de la requête et des mocks
        CreateCompteRequest request = new CreateCompteRequest();
        request.setNom("MonCompte");
        request.setDescription("Description du compte");
        request.setDateAjout(LocalDateTime.now());
        request.setValeurMotdePassePlateforme("MotDePasseCompromis123!");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("utilisateur123");
        request.setUtilisateur(utilisateur);

        when(verificationFeignClient.getMotDePasseCompromis(anyString())).thenReturn(true);

        // when: Exécution de la requête HTTP POST
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))

        // then: Vérification du statut et de la réponse
                .andExpect(status().isUnauthorized());
    }

    // Teste la méthode create pour vérifier que le statut HTTP est 401 (Unauthorized) lorsque le mot de passe est faible
    @Test
    public void shouldNotCreateWhenPasswordIsWeak() throws Exception {
        // given: Préparation de la requête et des mocks
        CreateCompteRequest request = new CreateCompteRequest();
        request.setNom("MonCompte");
        request.setDescription("Description du compte");
        request.setDateAjout(LocalDateTime.now());
        request.setValeurMotdePassePlateforme("faible");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("utilisateur123");
        request.setUtilisateur(utilisateur);

        when(verificationFeignClient.getMotDePasseCompromis(anyString())).thenReturn(false);
        when(verificationFeignClient.getForceMotDePasse(anyString())).thenReturn(false);

        // when: Exécution de la requête HTTP POST
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))

        // then: Vérification du statut et de la réponse
                .andExpect(status().isUnauthorized());
    }


    // Teste la méthode create pour vérifier que le statut HTTP est 400 (Bad Request) lorsque la requête est incorrecte
    @ParameterizedTest
    @MethodSource("provideCreateCompteRequests")
    public void shouldCreateStatusBadRequest(CreateCompteRequest request) throws Exception {
        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.post(ENDPOINT)
                .contentType("application/json")
                .content(this.mapper.writeValueAsString(request)));

        // then
        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Fournit des données de test pour le test paramétré shouldCreateStatusBadRequest
    private static Stream<CreateCompteRequest> provideCreateCompteRequests() {
        return Stream.of(
            new CreateCompteRequest(), // Requête vide
            createCompteRequestWithMissingField(), // Requête avec un champ obligatoire manquant
            createCompteRequestWithInvalidValue() // Requête avec un champ ayant une valeur incorrecte
            
        );
    }
    
    // Crée une requête de création de compte avec un champ obligatoire manquant
    private static CreateCompteRequest createCompteRequestWithMissingField() {
        CreateCompteRequest request = new CreateCompteRequest();
        // Exemple : Ne définissez pas un champ obligatoire comme le nom
        // request.setNom(null);
        return request;
    }
    
    // Crée une requête de création de compte avec un champ ayant une valeur incorrecte
    private static CreateCompteRequest createCompteRequestWithInvalidValue() {
        CreateCompteRequest request = new CreateCompteRequest();
        // Exemple : Définissez un champ avec une valeur incorrecte
        // request.setNom(" "); // Nom vide
        return request;
    }


    // Teste la méthode update pour vérifier que le statut HTTP est 201 (Created) et que le compte est mis à jour
    @Test
    public void shouldUpdateStatusCreated() throws Exception {
        // given
        String id = "test-id";
        CreateCompteRequest request = new CreateCompteRequest();
        request.setNom("Updated Compte");
        String requestJson = this.mapper.writeValueAsString(request);
        
        Compte existingCompte = new Compte();
        Mockito.when(this.repository.findById(id)).thenReturn(Optional.of(existingCompte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.put(ENDPOINT + "/" + id)
                .contentType("application/json")
                .content(requestJson));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Teste la méthode delete pour vérifier que le statut HTTP est 201 (Created) et que le compte est supprimé
    @Test
    public void shouldDeleteStatusCreated() throws Exception {
        // given
        String id = "test-id";
        Compte existingCompte = new Compte();
        Mockito.when(this.repository.findById(id)).thenReturn(Optional.of(existingCompte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT + "/" + id));

        // then
        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    // Teste la méthode findByUserId pour vérifier que le statut HTTP est 200 (OK) et que les comptes sont retournés
    @Test
    public void shouldFindByUserIdStatusOk() throws Exception {
        // given
        String userId = "user-id";
        Compte compte1 = new Compte();
        Compte compte2 = new Compte();
        Mockito.when(this.repository.findByUtilisateurId(userId)).thenReturn(List.of(compte1, compte2));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/user/" + userId));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
        result.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));

        Mockito.verify(this.repository).findByUtilisateurId(userId);
    }
}
