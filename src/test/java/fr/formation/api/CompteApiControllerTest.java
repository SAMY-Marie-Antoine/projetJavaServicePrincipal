package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.formation.model.Compte;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CreateCompteRequest;



@ExtendWith(MockitoExtension.class)
public class CompteApiControllerTest {

    private final static String ENDPOINT = "/api/compte";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @MockBean
    private CompteRepository repository;

    @InjectMocks
    private CompteApiController ctrl;

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

    // Teste la méthode findById pour vérifier que le statut HTTP est 200 (OK) et que le compte est retourné
    @Test
    public void shouldFindByIdStatusOk() throws Exception {
        // given
        

        // when

        // then
        
    }

    // Teste la méthode findById pour vérifier qu'elle retourne 404 si le compte n'existe pas
    @Test
    public void shouldFindByIdStatusNotFound() throws Exception {
        // given
        
        // when

        // then
        
    }

    // Teste la méthode create pour vérifier que le statut HTTP est 201 (Created) et que le compte est créé
    @Test
    public void shouldCreateStatusCreated() throws Exception {
        // given
        

        // when
        

        // then
        
    }

    // Teste la méthode create pour vérifier que le statut HTTP est 400 (Bad Request) lorsque la requête est incorrecte
    @ParameterizedTest
    @MethodSource("invalidCompteRequests")
    public void shouldCreateStatusBadRequest(CreateCompteRequest request) throws Exception {
        // when
        

        // then
        
    }

    // Fournit des données de test invalides pour la méthode create
    private static Stream<CreateCompteRequest> invalidCompteRequests() {
        CreateCompteRequest emptyRequest = new CreateCompteRequest();
        CreateCompteRequest invalidRequest = new CreateCompteRequest();
        // Add invalid fields
        return Stream.of(emptyRequest, invalidRequest);
    }

    // Teste la méthode delete pour vérifier que le statut HTTP est 201 (Created) et que le compte est supprimé
    @Test
    public void shouldDelete() throws Exception {
        // given
      

        // when

        // then
    }

    // Teste la méthode update pour vérifier que le statut HTTP est 201 (Created) et que le compte est mis à jour
    @Test
    public void shouldUpdate() throws Exception {
        // given
        

        // when
        
        // then
        
    }
}
