package fr.formation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.formation.model.Compte;
import fr.formation.repository.CompteRepository;
import fr.formation.request.CompteRequest;
import fr.formation.response.CompteResponse;

@RestController
@RequestMapping("/api/compte")
@CrossOrigin("*")
public class CompteApiController {
    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    //private CommentaireFeignClient commentaireFeignClient;

    @GetMapping
    public List<CompteResponse> findAll() {
        List<Compte> comptes = this.compteRepository.findAll();
        List<CompteResponse> response = new ArrayList<>();

        for (Compte compte : comptes) {
        	CompteResponse compteResponse = new CompteResponse();

            BeanUtils.copyProperties(compte, compteResponse);

            response.add(compteResponse);

            /*Integer note = this.commentaireFeignClient.getNoteByProduitId(compte.getId());
            
            if (note != null) {
            	compteResponse.setNote(note);
            }*/
        }
        
        return response;
    }
    


    @GetMapping("/{id}/name")
    public String getNameById(@PathVariable String id) {
        Optional<Compte> optCompte = this.compteRepository.findById(id);

        if (optCompte.isPresent()) {
            return optCompte.get().getNom();
        }

        return "- product not found -";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody CompteRequest request) {
        Compte compte = new Compte();
        
        BeanUtils.copyProperties(request, compte);

        this.compteRepository.save(compte);

        return compte.getId();
    }
}
