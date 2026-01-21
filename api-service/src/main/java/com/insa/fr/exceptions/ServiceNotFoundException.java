package com.insa.fr.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author herve
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    //retourner le message de l'exception soulevé par défaut.
    public ServiceNotFoundException(String message) { super(message);}
}
