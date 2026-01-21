package com.insa.fr.exceptions;

import com.insa.fr.entity.CustomErrorResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;


/**
 *
 * @author herve
 */

@ControllerAdvice
public class GlobalExceptionController {
   
private static final Logger log = LoggerFactory.getLogger(GlobalExceptionController.class);
    
    /***********************************
     * Gestion des erreurs spécifique applicatives
     * @param exception
     * @return 
     ***********************************/
   @ExceptionHandler(value = ServiceNotFoundException.class)
   public ResponseEntity<ApiError> exception(ServiceNotFoundException exception) 
   {
      
         List<String> details = new ArrayList<>();
         details.add(exception.getMessage());
               
         ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST,"Resource Not Found",details);
            
    return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
   }  
   
   /***********************************
    * Gestion des exceptions génériques
    * @param ex
    * @param request
    * @return 
    ************************************/
   
   @ExceptionHandler(Exception.class)
public final ResponseEntity<ApiError> handleAllExceptions(Exception ex, WebRequest request) {
     List<String> details = new ArrayList<>();
         details.add(request.getDescription(false));
  ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST,ex.getMessage(),details);
  return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
}

/***********************************
    * Gestion des exceptions génériques
    * @param ex
    * @param request
    * @return 
    ************************************/
    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public CustomErrorResponse handleNotFound(ResourceNotFoundException ex) {
        log.warn("Entity was not found", ex);
        return new CustomErrorResponse(ERROR_CODE.E0001.name(), ex.getMessage());
    }

/***********************************
    * Gestion des exceptions génériques
    * @param ex
    * @param request
    * @return 
    ************************************/
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(NotAllowedOperationException.class)
    @ResponseBody
    public CustomErrorResponse handleNotFound(NotAllowedOperationException ex) {
        log.warn("Not Allowed operation", ex);
        return new CustomErrorResponse(ERROR_CODE.E0002.name(), ex.getMessage());
    }

    private enum ERROR_CODE {E0001, E0002}
}
