package com.insa.fr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**********************
 *
 * @author tondeur-h
 **********************/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Students {
    
    int id;
    String nom;
    String prenom;
    String mail;
    String formation;
}
