package com.mikhaylova.lms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InternalServerError extends RuntimeException {
    public InternalServerError(String message) {
        super(message);
    }
}
