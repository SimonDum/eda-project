package com.insa.fr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 *
 * @author herve
 * **/
@Data
public class Logs 
{  
    @JsonIgnore
    String datelog; //date do log creer par le service
 
    String service;  //quel service nom de l'application
    String level;  //niveau du log
    String trying;  //ce que j'essaye de faire
    String message;  //le message qui suis l'action
}