package com.insa.fr;

import com.insa.fr.dto.StudentRequest;
import com.insa.fr.dto.StudentResponse;
import com.insa.fr.entity.Students;
import com.insa.fr.services.Services_implements;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * INTEGRATION SERVICE CONTROLLER (Architecture EDA)
 * ------------------------------------------------
 * Ce service est le coeur du traitement métier asynchrone.
 * Il agit en tant que Consommateur des évènement de requête et Producteur des évènements de réponse.
 * * Son cycle de vie est :
 * 1. Écouter les événements entrants sur le topic de requête (etudiants-request).
 * 2. Exécuter l'action demandée en Base de Données.
 * 3. Publier le résultat (Succès/Données/Erreur) sur le topic de réponse (etudiants-responses).
 */
@Service
public class Controller {

    @Autowired
    private Services_implements dbService;

    @Autowired
    private KafkaTemplate<String, StudentResponse> kafkaTemplate;

    @Value("${app.kafka.topic.response}")
    private String topicResponse;

    /**
     * Les messages reçus sur le topic request suivent le format défini dans StudentRequest.
     * Les réponses envoyées sur le topic response suivent le format défini dans StudentResponse.
     * Ces classes sont partagées avec le service API.
     * (La désérialisation et la sérialisation se font automatiquement)
    */
    @KafkaListener(topics = "${app.kafka.topic.request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(StudentRequest event) {

        // Préparation de la réponse
        StudentResponse response = new StudentResponse();
        response.setAction(event.getAction());

        try {
            
            System.out.println("Message reçu du bus : " + event);

            switch (event.getAction()) {
                case GET_ALL:
                    // On récupère la liste depuis la bdd et on l'envoie sur le topic
                    List<Students> allStudents = dbService.getStudents();   
                    response.setData(allStudents);
                    response.setSuccess(true);
                    response.setMessage("Liste OK");
                    System.out.println("--> Réponse envoyée sur le topic : " + topicResponse);
                    break;

                case CREATE:
                    // On crée l'étudiant sur la bdd et on envoie une réponse de succès ou d'échec
                    if(event.getStudent() != null) {
                        int id = dbService.createStudent(event.getStudent());
                        response.setSuccess(id != -1);
                        response.setAffectedId(id);
                        System.out.println("--> Succès : Etudiant créé avec ID " + id);
                    }
                    break;
                    
                case UPDATE:
                    // On modifie l'étudiant sur la bdd et on envoie une réponse de succès ou d'échec
                    if(event.getStudent() != null && event.getTargetId() != null && !event.getTargetId().isBlank()) {
                        boolean success = dbService.updateStudent(event.getStudent(), event.getTargetId());
                        response.setSuccess(success);
                        System.out.println("--> Succès : Etudiant mis à jour");
                    }
                    break;
                    
                case DELETE:
                    // On supprime l'étudiant sur la bdd et on envoie une réponse de succès ou d'échec
                    if(event.getTargetId() != null && !event.getTargetId().isBlank()) {
                        boolean success = dbService.deleteStudent(event.getTargetId());
                        response.setSuccess(success);
                        System.out.println("--> Succès : Etudiant supprimé");
                    }
                    break;
                    
                default:
                    // Par défaut on envoie une réponse d'erreur
                    System.out.println("Action inconnue : " + event.getAction());
                    response.setSuccess(false);
                    response.setMessage("Action inconnue");
            }

        } catch (Exception e) {
            // On envoie une réponse d'erreur
            response.setSuccess(false);
            response.setMessage("Erreur : " + e.getMessage());
            e.printStackTrace();
        }

        kafkaTemplate.send(topicResponse, response);
    }
}