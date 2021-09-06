package com.mikhaylova.lms.dto;

import com.mikhaylova.lms.annotation.AdminMustHaveRoleAdmin;
import com.mikhaylova.lms.annotation.UniqueEmailOrNotChanged;
import com.mikhaylova.lms.annotation.UniqueUsernameOrNotChanged;
import com.mikhaylova.lms.domain.Course;
import com.mikhaylova.lms.domain.Role;
import com.mikhaylova.lms.domain.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@UniqueUsernameOrNotChanged
@UniqueEmailOrNotChanged
@AdminMustHaveRoleAdmin
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {

    Long id;

    @NotBlank(message = "Имя пользователя должно быть заполнено")
    @Pattern(regexp = "^[A-Za-z!@$%^&(){}:;<>,.?/~_+-=|]*$",
            message = "Логин должен содержать только латиницу и/или спец.символы")
    String username;

    @Pattern(regexp = "^[A-Za-zА-Яа-я]*$",
            message = "Имя может содержать латиницу и/или кириллицу")
    String firstName;

    @Pattern(regexp = "^[A-Za-zА-Яа-я]*$",
            message = "Фамилия может содержать латиницу и/или кириллицу")
    String lastName;

    @Pattern(regexp = "^$|^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
            message = "Введен некорректный номер телефона")
    String phoneNumber;

    @Email(message = "Некорректный электронный адрес")
    String email;

    Set<Course> courses;

    @NotEmpty(message = "Выберите хотя бы одну роль")
    Set<Role> roles;

    @Pattern(regexp = "^[A-Za-z!@$%^&(){}:;<>,.?/~_+-=|]{8,}$|^$",
            message = "Пароль должен содержать только латиницу и/или спец.символы и содержать не менее 8 символов")

    String password;

    Date modificationDate;

    User modificationAuthor;

    public UserDto(Long id, @NotBlank(message = "Имя пользователя должно быть заполнено") String username, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserDto(Long id, @NotBlank(message = "Имя пользователя должно быть заполнено") String username) {
        this.id = id;
        this.username = username;
    }

    public UserDto(Long id, @NotBlank(message = "Имя пользователя должно быть заполнено") String username, String firstName, String lastName, String phoneNumber, String email, Set<Course> courses, @NotEmpty(message = "Выберите хотя бы одну роль") Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.courses = courses;
        this.roles = roles;
    }
}
