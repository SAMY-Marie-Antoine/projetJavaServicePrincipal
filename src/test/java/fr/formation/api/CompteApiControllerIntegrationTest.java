package fr.formation.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.formation.model.Compte;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CreateCompteRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/comptes.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear-all.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class CompteApiControllerIntegrationTest {
    
    private final static String ENDPOINT = "/api/compte";

    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @SpyBean
    private CompteRepository repository;

    @BeforeEach
    public void beforeEach() {
        this.mapper = new ObjectMapper();
    }

    // Teste la récupération de tous les comptes sans authentification
    @Test
    void shouldFindAllStatusUnauthorized() throws Exception {
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Teste la récupération de tous les comptes avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldFindAllStatusOk() throws Exception {
        
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2))); // Pour vérifier son id en sortie
    }

    // Teste la création d'un nouveau compte sans authentification
    @Test
    void shouldCreateStatusCreatedUnauthorized() throws Exception {
        
        // given

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT));

        // then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    // Teste la création d'un nouveau compte avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldCreateStatusCreated() throws Exception {
        
        // given
        ArgumentCaptor<Compte> compteCaptor = ArgumentCaptor.forClass(Compte.class);
        CreateCompteRequest request = new CreateCompteRequest();

        request.setNom("Nom test");

        // when
        ResultActions result = this.mockMvc.perform(
            MockMvcRequestBuilders
                .post(ENDPOINT)
                .content(this.mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        Mockito.verify(repository).save(compteCaptor.capture()); // On capture le compte passé en tant qu'argument de la méthode save

        Compte compte = compteCaptor.getValue();

        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(compte.getId())); // Pour vérifier son id en sortie
    }
    

    // Teste la récupération du nom d'un compte par son ID avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldGetNameById() throws Exception {
        
        // given
        Compte compte = new Compte();
        compte.setId("1");
        compte.setNom("Compte 1");

        when(repository.findById("1")).thenReturn(Optional.of(compte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1/name"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(MockMvcResultMatchers.content().string("Compte 1"));
    }

    // Teste la récupération d'un compte par son ID avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldFindById() throws Exception {
        
        // given
        Compte compte = new Compte();
        compte.setId("1");
        compte.setNom("Compte 1");

        when(repository.findById("1")).thenReturn(Optional.of(compte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/1"));

        // then
        /*result.andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(jsonPath("$.id", is("1")))
              .andExpect(jsonPath("$.nom", is("Compte 1")));
              */
    }

    // Teste la mise à jour d'un compte existant avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldUpdate() throws Exception {
        
        // given
        Compte compte = new Compte();
        compte.setId("1");
        compte.setNom("Compte 1");

        CreateCompteRequest request = new CreateCompteRequest();
        request.setNom("Compte 1 Modifié");

        when(repository.findById("1")).thenReturn(Optional.of(compte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.put(ENDPOINT + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    // Teste la suppression d'un compte par son ID avec authentification
    @Test
    //@WithMockUser(roles = "USER")
    void shouldDelete() throws Exception {
        
        // given
        Compte compte = new Compte();
        compte.setId("1");

        when(repository.findById("1")).thenReturn(Optional.of(compte));

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.delete(ENDPOINT + "/1"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isCreated())
              .andExpect(MockMvcResultMatchers.content().string("1"));
    }


    // Teste la récupération des comptes d'un utilisateur spécifique par son ID avec authentification
    //@Test
    //@WithMockUser(roles = "USER")
    /*void shouldFindByUserId() throws Exception {

        // given
        List<Compte> comptes = new ArrayList<>();
        Compte compte = new Compte();
        compte.setId("1");
        compte.setUtilisateurId("user1");
        comptes.add(compte);

        when(repository.findByUtilisateurId("user1")).thenReturn(comptes);

        // when
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders.get(ENDPOINT + "/user/user1"));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].id", is("1")))
              .andExpect(jsonPath("$[0].utilisateurId", is("user1")));
    }*/


}
