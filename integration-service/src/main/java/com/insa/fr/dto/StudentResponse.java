package com.insa.fr.dto;

import com.insa.fr.entity.Students;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse implements Serializable {
    private StudentAction action;
    private boolean success;
    private String message;
    private List<Students> data;
    private Integer affectedId;
}