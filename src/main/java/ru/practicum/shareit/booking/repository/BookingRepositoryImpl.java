package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Booking save(Booking booking) {
        if (booking.getId() == null) {
            booking.setId(idCounter.getAndIncrement());
        }
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findByBookerId(Long bookerId, String state, int from, int size) {
        return filterBookings(
                bookings.values().stream()
                        .filter(b -> b.getBooker().getId().equals(bookerId))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList()),
                state, from, size);
    }

    @Override
    public List<Booking> findByItemOwnerId(Long ownerId, String state, int from, int size) {
        return filterBookings(
                bookings.values().stream()
                        .filter(b -> b.getItem().getOwnerId().equals(ownerId))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .collect(Collectors.toList()),
                state, from, size);
    }

    @Override
    public List<Booking> findByItemIdAndEndBefore(Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getEnd().isBefore(now))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemIdAndStartAfter(Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status) {
        return bookings.values().stream()
                .filter(b -> b.getItem().getId().equals(itemId))
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now) {
        return bookings.values().stream()
                .anyMatch(b -> b.getBooker().getId().equals(bookerId)
                        && b.getItem().getId().equals(itemId)
                        && b.getEnd().isBefore(now));
    }

    private List<Booking> filterBookings(List<Booking> bookings, String state, int from, int size) {
        List<Booking> filtered = bookings.stream()
                .filter(b -> filterByState(b, state))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());

        return filtered;
    }

    private boolean filterByState(Booking booking, String state) {
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "ALL":
                return true;
            case "CURRENT":
                return booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
            case "PAST":
                return booking.getEnd().isBefore(now);
            case "FUTURE":
                return booking.getStart().isAfter(now);
            case "WAITING":
                return booking.getStatus() == BookingStatus.WAITING;
            case "REJECTED":
                return booking.getStatus() == BookingStatus.REJECTED;
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

}