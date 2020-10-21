package uk.ac.ed.inf.aqmaps.client;

import java.io.IOException;

public class HTTPException extends IOException {
    private static final long serialVersionUID = 1L;

    public HTTPException(int errorStatusCode, String message){
        super(message);
    }
}    
