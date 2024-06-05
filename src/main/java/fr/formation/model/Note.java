package fr.formation.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "note")
public class Note {

    @Id
	@UuidGenerator
	private String id;
    
    @Column(name="nom", nullable = false)
    private String nom;

    @Column(name="description")
    private String description;

    @Column(name="date_ajout")
    private LocalDateTime dateAjout; 

    @Column(name="date_modif")
    private LocalDateTime dateModif;  

    @Column(name="contenu")
    private String contenu;

    @ManyToOne()
    private Utilisateur utilisateur;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getNom() {
        return nom;
    }


    public void setNom(String nom) {
        this.nom = nom;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDateTime getDateAjout() {
        return dateAjout;
    }


    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }


    public LocalDateTime getDateModif() {
        return dateModif;
    }


    public void setDateModif(LocalDateTime dateModif) {
        this.dateModif = dateModif;
    }


    public String getContenu() {
        return contenu;
    }


    public void setContenu(String contenu) {
        this.contenu = contenu;
    }


    public Utilisateur getUtilisateur() {
        return utilisateur;
    }


    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    

}
