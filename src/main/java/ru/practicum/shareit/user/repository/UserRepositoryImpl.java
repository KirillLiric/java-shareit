package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> emailIndex = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }

    private User create(User user) {
        validateEmail(user.getEmail());

        long id = idCounter.getAndIncrement();
        user.setId(id);
        users.put(id, user);
        emailIndex.put(user.getEmail().toLowerCase(), user);
        return user;
    }

    private User update(User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new ValidationException("User not found with id: " + user.getId());
        }

        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            validateEmail(user.getEmail());
            // Удаляем старый email из индекса
            emailIndex.remove(existingUser.getEmail().toLowerCase());
            // Добавляем новый
            emailIndex.put(user.getEmail().toLowerCase(), user);
        }

        // Обновляем данные
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (emailIndex.containsKey(email.toLowerCase())) {
            throw new ValidationException("Email already exists: " + email);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        User user = users.get(id);
        if (user != null) {
            users.remove(id);
            emailIndex.remove(user.getEmail().toLowerCase());
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return email != null && emailIndex.containsKey(email.toLowerCase());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(emailIndex.get(email.toLowerCase()));
    }
}