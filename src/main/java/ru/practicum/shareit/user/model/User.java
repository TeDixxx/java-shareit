package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class User {
    private Long id;

    @NotEmpty
    @NotBlank
    private String name;

    @NotEmpty(message = "Email can't be null")
    @Email(message = "Not valid email")
    @NotBlank
    private String email;
}
