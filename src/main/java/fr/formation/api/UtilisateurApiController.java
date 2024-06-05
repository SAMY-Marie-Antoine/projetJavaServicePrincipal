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

import fr.formation.model.Utilisateur;
import fr.formation.repository.UtilisateurRepository;
import fr.formation.request.UtilisateurRequest;
import fr.formation.response.UtilisateurResponse;

@RestController
@RequestMapping("/api/utilisateur")
@CrossOrigin("*")
public class UtilisateurApiController {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    //private CommentaireFeignClient commentaireFeignClient;

    @GetMapping
    public List<UtilisateurResponse> findAll() {
        List<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
        List<UtilisateurResponse> response = new ArrayList<>();

        for (Utilisateur utilisateur : utilisateurs) {
        	UtilisateurResponse compteResponse = new UtilisateurResponse();

            BeanUtils.copyProperties(utilisateur, compteResponse);

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
        Optional<Utilisateur> optUtilisateur = this.utilisateurRepository.findById(id);

        if (optUtilisateur.isPresent()) {
            return optUtilisateur.get().getNom();
        }

        return "- product not found -";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String create(@RequestBody UtilisateurRequest request) {
    	Utilisateur utilisateur = new Utilisateur();
        
        BeanUtils.copyProperties(request, utilisateur);

        this.utilisateurRepository.save(utilisateur);

        return utilisateur.getId();
    }
}
