package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking create(Booking booking, Long bookerId, Long itemId) {
        validateBooking(booking, bookerId, itemId);

        booking.setBooker(userService.getById(bookerId));
        booking.setItem(itemService.getById(itemId));
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Подтверждать бронирование может только владелец");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже было обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("Просмотр бронирования доступен только автору или владельцу");
        }

        return booking;
    }

    @Override
    public List<Booking> getBookingsByBooker(Long bookerId, String state, int from, int size) {
        userService.getById(bookerId);
        return bookingRepository.findByBookerId(bookerId, state, from, size);
    }

    @Override
    public List<Booking> getBookingsByOwner(Long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        return bookingRepository.findByItemOwnerId(ownerId, state, from, size);
    }

    private void validateBooking(Booking booking, Long bookerId, Long itemId) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }

        if (booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException("Даты начала и окончания не могут совпадать");
        }

        var item = itemService.getById(itemId);
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }

        if (item.getOwnerId().equals(bookerId)) {
            throw new NotFoundException("Владелец не может бронировать свой предмет");
        }

        // Проверка на пересечение с другими бронированиями
        boolean hasOverlappingBookings = bookingRepository.findByItemIdAndStatus(itemId, BookingStatus.APPROVED).stream()
                .anyMatch(b -> isOverlapping(booking, b));

        if (hasOverlappingBookings) {
            throw new ValidationException("Предмет уже забронирован на указанные даты");
        }
    }

    private boolean isOverlapping(Booking newBooking, Booking existingBooking) {
        return newBooking.getStart().isBefore(existingBooking.getEnd()) &&
                newBooking.getEnd().isAfter(existingBooking.getStart());
    }
}
