package com.insa.fr.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 *
 * @author herve
 */
@Data
@AllArgsConstructor
public class ApiError 
{
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
            private LocalDateTime timestamp;
            private HttpStatus status;
            private String message;
            private List errors;
}