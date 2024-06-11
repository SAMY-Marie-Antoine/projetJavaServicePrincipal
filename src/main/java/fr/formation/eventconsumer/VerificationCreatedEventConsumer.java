package fr.formation.eventconsumer;

import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import fr.formation.event.VerificationCreatedEvent;
import fr.formation.model.Utilisateur;
import fr.formation.repository.UtilisateurRepository;

@Component("onverificationCreated")
public class VerificationCreatedEventConsumer implements Consumer<VerificationCreatedEvent> {
    @Autowired
    private UtilisateurRepository repository;

    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void accept(VerificationCreatedEvent evt) {
        Optional<Utilisateur> optUtilisateur = this.repository.findById(evt.getUtilisateurId());

        if (optUtilisateur.isPresent()) {
            // C'est notable !
            this.streamBridge.send("verification.validated", evt.getVerificatonId());
        }
        
        else {
            // C'est pas notable !
            this.streamBridge.send("verification.rejected", evt.getVerificatonId());
        }
    }
}
