package com.insa.fr;

import com.insa.fr.entity.Alive;
import com.insa.fr.entity.Students;
import com.insa.fr.exceptions.NotAllowedOperationException;
import com.insa.fr.services.Services_Interface;
import com.insa.fr.tools.ApiKey_Secure;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API SERVICE CONTROLLER (Architecture EDA)
 * ------------------------------------------------
 * Ce contrôleur agit comme une porte d'entrée (Gateway) asynchrone.
 * Contrairement à une API REST classique, il ne traite pas la demande lui-même.
 * Il est simplement le Producteur des évènements de requête.
 * * Son rôle est uniquement de :
 * 1. Valider la sécurité (API Key).
 * 2. Transmettre l'événement au bus de messages Kafka sur le topic de requête (etudiants-request).
 * 3. Renvoyer immédiatement un accusé de réception (ACK - 202 Accepted).
 */
@RestController
@CrossOrigin(origins = "*") 
public class Controller {

    private final String VERSION = "1.0-EDA";
    private final String WHOAMI = "api-service";

    @Autowired
    private Services_Interface studentService;

    @Autowired
    private ApiKey_Secure securityTool;

    // --- Health Check ---
    // On a rendu la route de health check publique contrairement au code initial
    // car on l'utilise dans le readiness probe du fichier yaml kubernetes
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Vérifier l'état du service API")
    public Alive iamAlive() {
        return new Alive("200", VERSION, WHOAMI);
    }

    // --- Endpoints Métier ---
    @PostMapping(value = "/service/student/add", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Demander la création d'un étudiant (Async)")
    public ResponseEntity<String> insertStudent(@RequestBody Students stud, 
                                                @RequestHeader(value = "x-api-key") String xapikey) {
        if (securityTool.verif_apikey(xapikey)) {
            
            // On pousse l'évènement dans le bus
            studentService.createStudent(stud);

            // On renvoie un accusé de réception
            return new ResponseEntity<>(
                "{\"status\":\"ACCEPTED\", \"message\":\"Demande de création envoyée au bus Kafka\"}", 
                HttpStatus.ACCEPTED
            );
        } else {
            throw new NotAllowedOperationException("Accès refusé");
        }
    }

    @DeleteMapping(value = "/service/student/delete/{id}")
    @Operation(summary = "Demander la suppression d'un étudiant (Async)")
    public ResponseEntity<String> deleteStudent(@PathVariable("id") String id, 
                                                @RequestHeader(value = "x-api-key") String xapikey) {
        if (securityTool.verif_apikey(xapikey)) {
            
            // On pousse l'évènement dans le bus
            studentService.deleteStudent(id);
            
            // On renvoie un accusé de réception
            return new ResponseEntity<>(
                "{\"status\":\"ACCEPTED\", \"message\":\"Demande de suppression envoyée\"}", 
                HttpStatus.ACCEPTED
            );
        } else {
            throw new NotAllowedOperationException("Accès refusé");
        }
    }
    
    @GetMapping(value = "/service/students")
    public ResponseEntity<String> getAll(@RequestHeader(value = "x-api-key") String xapikey) {
         if (securityTool.verif_apikey(xapikey)) {

            // On pousse l'évènement dans le bus
            studentService.triggerGetAll();
            
            // On renvoie un accusé de réception
            return new ResponseEntity<>(
                "{\"status\":\"ACCEPTED\", \"message\":\"Demande de rafraichissement envoyée\"}", 
                HttpStatus.ACCEPTED
            );
         }
         throw new NotAllowedOperationException("Accès refusé");
    }

    @PutMapping(value = "/service/student/update", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Demander la modification d'un étudiant (Async)")
    public ResponseEntity<String> updateStudent(@RequestBody Students stud, 
                                                @RequestHeader(value = "x-api-key") String xapikey) {
        if (securityTool.verif_apikey(xapikey)) {
            
            // On pousse l'évènement dans le bus
            studentService.updateStudent(stud);

            // On renvoie un accusé de réception
            return new ResponseEntity<>(
                "{\"status\":\"ACCEPTED\", \"message\":\"Demande de modification envoyée\"}", 
                HttpStatus.ACCEPTED
            );
        } else {
            throw new NotAllowedOperationException("Accès refusé");
        }
    }
}