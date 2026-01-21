package com.insa.fr.dto;

import com.insa.fr.entity.Students;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor // Génère le constructeur vide obligatoire pour Jackson
@AllArgsConstructor
public class StudentRequest implements Serializable {
    private StudentAction action;
    private Students student;
    private String targetId;
}