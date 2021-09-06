package com.mikhaylova.lms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotAssignedToCourseException extends RuntimeException {
    public UserNotAssignedToCourseException(String message) {
        super(message);
    }
}
