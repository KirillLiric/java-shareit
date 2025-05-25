package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static Booking toEntity(BookingDto bookingDto) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                null,
                null,
                BookingStatus.WAITING
        );
    }

    public static BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                ItemMapper.toDto(booking.getItem()),
                UserMapper.toDto(booking.getBooker())
        );
    }
}