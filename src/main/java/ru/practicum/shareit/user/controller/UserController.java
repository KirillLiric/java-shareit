package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDto userDto) {
        try {
            User user = UserMapper.toEntity(userDto);
            User createdUser = userService.create(user);
            return ResponseEntity.ok(UserMapper.toDto(createdUser));
        } catch (ValidationException e) {
            Map<String, String> errorResponse = Map.of(
                    "error", "Conflict",
                    "message", e.getMessage(),
                    "status", "409"
            );
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User updatedUser = userService.update(userId, user);
        return UserMapper.toDto(updatedUser);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        return UserMapper.toDto(userService.getById(userId));
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}