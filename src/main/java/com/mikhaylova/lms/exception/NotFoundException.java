package com.mikhaylova.lms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class NotFoundException extends RuntimeException {
    private String className;
    private Long id;
    private String name;

    public NotFoundException(String className, Long id) {
        this.className = className;
        this.id = id;
    }

    public NotFoundException(String className, String name) {
        this.className = className;
        this.name = name;
    }
}
