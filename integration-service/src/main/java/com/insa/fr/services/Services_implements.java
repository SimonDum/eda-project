package com.insa.fr.services;

import com.insa.fr.entity.Students;
import com.insa.fr.mappers.StudentMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author herve
 */

/**
 * Implémentation des fonctions pour effectuer les opérations CRUD en base de données
 */

@Service
public class Services_implements implements Services_Interface {
    
    //autowired le service jdbc
    @Autowired
    JdbcTemplate jdbctemplate=new JdbcTemplate();
        
     
     /****************************************
      * Un service
      * @return 
      ****************************************/
    @Override
   public List<Students> getStudents()
   {
        List<Students> lstud=new ArrayList<>();

        try
        {

            String sql="select id,nom,prenom,mail,formation from students";
            List<Map<String, Object>> mstud=jdbctemplate.queryForList(sql);
        if (!mstud.isEmpty())
       {
           mstud.forEach(row -> {
               //alimenter l'objet
               Students tstud=new Students();
               //ajouter l'objet a la liste...
               lstud.add(new StudentMapper().mapping(tstud, row));
            });
       }
        }
        catch (DataAccessException dae)
        {
            return new ArrayList<>();
        }
        return lstud;
   }

   
    @Override
    public List<Students> getStudent(String id) {
           List<Students> lstud=new ArrayList<>();
        try
        {

            String sql="select id,nom,prenom,mail,formation from students where id='"+id+"'";
            List<Map<String, Object>> mstud=jdbctemplate.queryForList(sql);
        if (!mstud.isEmpty())
       {
           mstud.forEach(row -> {
               //alimenter l'objet
               Students tstud=new Students();
               //ajouter l'objet a la liste...
               lstud.add(new StudentMapper().mapping(tstud, row));
            });
       }
        }
        catch (DataAccessException dae)
        { 
            return new ArrayList<>();
        }
        return lstud;
    }

    @Override
    public boolean deleteStudent(String id) {
        try{
        String sql="delete from students where id='"+id+"'";
         jdbctemplate.execute(sql);
        }
        catch(DataAccessException e){return false;}
        return true;
    }

    @Override
    public int createStudent(Students stud) {
        Integer idStudent=-1;
        try{

        //String sql="insert into students(id,nom,prenom,mail,formation) values('"+stud.getId()+"','"+stud.getNom()+"','"+stud.getPrenom()+"','"+stud.getMail()+"','"+stud.getFormation()+"')";
        String sql="insert into students(nom,prenom,mail,formation) values('"+stud.getNom()+"','"+stud.getPrenom()+"','"+stud.getMail()+"','"+stud.getFormation()+"') RETURNING id";
        idStudent=jdbctemplate.queryForObject(sql,Integer.class);
        }
        catch(DataAccessException e){return -1;}
        return idStudent;
    }

    @Override
    public boolean updateStudent(Students stud, String id) {
         try{
        String sql="update students set nom='"+stud.getNom()+"',prenom='"+stud.getPrenom()+"',mail='"+stud.getMail()+"',formation='"+stud.getFormation()+"' where id='"+id+"'";
        jdbctemplate.execute(sql);
        }
        catch(DataAccessException e){return false;}
        return true;
        
    }
    
}