package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(
            @RequestBody BookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        Booking booking = BookingMapper.toEntity(bookingDto);
        Booking createdBooking = bookingService.create(booking, bookerId, bookingDto.getItemId());
        return BookingMapper.toResponseDto(createdBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        Booking booking = bookingService.approve(bookingId, ownerId, approved);
        return BookingMapper.toResponseDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingService.getById(bookingId, userId);
        return BookingMapper.toResponseDto(booking);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByBooker(bookerId, state, from, size).stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.getBookingsByOwner(ownerId, state, from, size).stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}