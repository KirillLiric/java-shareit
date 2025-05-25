package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingService {

    Booking create(Booking booking, Long bookerId, Long itemId);

    Booking approve(Long bookingId, Long ownerId, boolean approved);

    Booking getById(Long bookingId, Long userId);

    List<Booking> getBookingsByBooker(Long bookerId, String state, int from, int size);

    List<Booking> getBookingsByOwner(Long ownerId, String state, int from, int size);

}