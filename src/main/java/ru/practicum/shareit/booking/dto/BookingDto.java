package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;

    @NotNull(message = "ID предмета не может быть пустым")
    private Long itemId;

    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    @NotNull(message = "Дата начала не может быть пустой")
    private LocalDateTime start;

    @Future(message = "Дата окончания должна быть в будущем")
    @NotNull(message = "Дата окончания не может быть пустой")
    private LocalDateTime end;
}