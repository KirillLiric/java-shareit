package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findByBookerId(Long bookerId, String state, int from, int size);

    List<Booking> findByItemOwnerId(Long ownerId, String state, int from, int size);

    List<Booking> findByItemIdAndEndBefore(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStartAfter(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);
}