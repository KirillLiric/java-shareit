package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

private Long id;

@NotBlank(message = "Описание запроса не может быть пустым")
private String description;

private LocalDateTime created;

}