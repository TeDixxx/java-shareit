package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty(message = "Name can't be empty")
    private String name;
    @NotEmpty(message = "Description can't be empty")
    private String description;
    @NotNull
    private Boolean available;

}
