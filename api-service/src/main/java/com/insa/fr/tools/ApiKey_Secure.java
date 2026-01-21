package com.insa.fr.tools;

import com.insa.fr.exceptions.NotAllowedOperationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKey_Secure {

    @Value("${API_MASTER_KEY}") 
    private String validKey;

    public boolean verif_apikey(String xapikey) {
        if (xapikey == null || !xapikey.equals(validKey)) {
            return false;
        }
        return true;
    }
}