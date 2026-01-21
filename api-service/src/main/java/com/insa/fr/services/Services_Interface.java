package com.insa.fr.services;

import com.insa.fr.entity.Students;
import java.util.List;

public interface Services_Interface {
    void createStudent(Students stud);
    void deleteStudent(String id);
    void triggerGetAll(); 
    void updateStudent(Students stud);
}