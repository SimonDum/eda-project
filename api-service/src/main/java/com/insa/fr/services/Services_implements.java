package com.insa.fr.services;

import com.insa.fr.dto.StudentAction;
import com.insa.fr.dto.StudentRequest;
import com.insa.fr.entity.Students;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Implémentation des fonctions d'envoi d'évènement de requête CRUD (à consommer par le service d'intégration)
 * 
 * Les messages envoyés sur le topic request suivent le format défini dans StudentRequest, classe partagée avec le service d'intégration
 * (La sérialisation se fait automatiquement)
 */

@Service
public class Services_implements implements Services_Interface {

    @Autowired
    private KafkaTemplate<String, StudentRequest> kafkaTemplate;

    @Value("${app.kafka.topic.request}")
    private String topicRequest;

    @Override
    public void createStudent(Students stud) {
        // Création de l'événement
        StudentRequest message = new StudentRequest(StudentAction.CREATE, stud, null);
        
        // Envoi asynchrone dans le bus
        kafkaTemplate.send(topicRequest, message);
        
        System.out.println("-> [API] Message CREATE envoyé pour : " + stud.getNom());
    }

    @Override
    public void deleteStudent(String id) {
        StudentRequest message = new StudentRequest(StudentAction.DELETE, null, id);
        kafkaTemplate.send(topicRequest, message);
        System.out.println("-> [API] Message DELETE envoyé pour ID : " + id);
    }

    @Override
    public void triggerGetAll() {
        StudentRequest message = new StudentRequest(StudentAction.GET_ALL, null, null);
        kafkaTemplate.send(topicRequest, message);
        System.out.println("-> [API] Message GET_ALL envoyé");
    }

    @Override
    public void updateStudent(Students stud) {
        StudentRequest message = new StudentRequest();
        message.setStudent(stud);
        message.setTargetId(Integer.toString(stud.getId()));
        message.setAction(StudentAction.UPDATE);
        kafkaTemplate.send(topicRequest, message);
        System.out.println("-> [API] Event UPDATE envoyé pour l'étudiant ID: " + stud.getId());
    }
}