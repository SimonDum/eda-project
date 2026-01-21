package com.insa.fr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Alive {
    private String code;
    private String version;
    private String serviceName;
}