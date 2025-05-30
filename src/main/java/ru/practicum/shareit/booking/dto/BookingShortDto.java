package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor // Добавляем публичный конструктор
public class BookingShortDto {
    private Long id;
    private Long bookerId;
}