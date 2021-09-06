package com.mikhaylova.lms.dto;

import com.mikhaylova.lms.annotation.IdenticalPasswords;
import com.mikhaylova.lms.annotation.UniqueEmail;
import com.mikhaylova.lms.annotation.UniqueUsername;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdenticalPasswords
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterUserDto {

    @NotBlank(message = "Имя пользователя должно быть заполнено")
    @Pattern(regexp = "^[A-Za-z!@$%^&(){}:;<>,.?/~_+-=|]*$",
            message = "Логин должен содержать только латиницу и/или спец.символы")
    @UniqueUsername
    String username;

    @NotBlank(message = "Email должен быть заполнен")
    @Email(message = "Некорректный электронный адрес")
    @UniqueEmail
    String email;

    @NotBlank(message = "Пароль должен быть заполнен")
    @Length(min = 8, message = "Длина пароля должна быть как минимум 8 символов")
    @Pattern(regexp = "^[A-Za-z!@$%^&(){}:;<>,.?/~_+-=|]*$",
            message = "Пароль должен содержать только латиницу и/или спец.символы")
    String password;

    @NotBlank(message = "Пароль должен быть заполнен")
    String repeatedPassword;

    public RegisterUserDto(@NotBlank(message = "Имя пользователя должно быть заполнено") String username,
                           @NotBlank(message = "Пароль должен быть заполнен") String password,
                           @NotBlank(message = "Пароль должен быть заполнен") String repeatedPassword) {
        this.username = username;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
    }
}
