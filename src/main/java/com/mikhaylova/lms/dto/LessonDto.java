package com.mikhaylova.lms.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class LessonDto {

    Long id;

    @NotBlank(message = "Название урока должно быть заполнено")
    String title;

    @NotBlank(message = "Содержание урока должно быть заполнено")
    String text;

    Long courseId;

    public LessonDto(Long courseId) {
        this.courseId = courseId;
    }

    public LessonDto(Long id, String title, Long courseId) {
        this.id = id;
        this.title = title;
        this.courseId = courseId;
    }

}