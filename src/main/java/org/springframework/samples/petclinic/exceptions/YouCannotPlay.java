package org.springframework.samples.petclinic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@Getter
public class YouCannotPlay extends RuntimeException {

    private static final long serialVersionUID = -1234567890123456789L;

    public YouCannotPlay(String message) {
        super(message);
    }

    public YouCannotPlay(String message, Throwable cause) {
        super(message, cause);
    }
}