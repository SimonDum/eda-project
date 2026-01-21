package com.insa.fr.mappers;

import com.insa.fr.entity.Students;
import java.util.Map;

/**********************
 *
 * @author tondeur-h
 **********************/
public class StudentMapper 

{

    /*idutilisateur, refad, nom, prenom, droit, codeuf, titre, login, motdepasse, loginad, actif, typeconnection*/
public Students mapping(Students tstudent, Map<String,Object> row)
{
            tstudent.setId(Integer.parseInt(row.get("id").toString(),10));
            tstudent.setNom(row.get("nom").toString());
            tstudent.setPrenom(row.get("prenom").toString());
            tstudent.setMail(row.get("mail").toString());
            tstudent.setFormation(row.get("formation").toString());
    return tstudent;
}
    
}
