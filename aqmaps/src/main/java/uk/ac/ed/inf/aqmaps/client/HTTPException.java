package uk.ac.ed.inf.aqmaps.client;

import java.io.IOException;

/**
 * An exception thrown whenever an unexpected http status is returned
 */
public class HTTPException extends IOException {
    private static final long serialVersionUID = 1L;

    public HTTPException(int errorStatusCode, String message){
        super(message);
    }
}    
