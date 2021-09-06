package com.mikhaylova.lms.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class CourseDto {

    Long id;

    @NotBlank(message = "Автор курса должен быть заполнен")
    String author;

    @NotBlank(message = "Название курса должно быть заполнено")
    String title;
}
