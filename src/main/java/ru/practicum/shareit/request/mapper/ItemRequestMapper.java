package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated()
        );
    }

    public static ItemRequest toEntity(ItemRequestDto requestDto, User requester) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                requester,
                requestDto.getCreated() != null ? requestDto.getCreated() : LocalDateTime.now()
        );
    }

    public static ItemRequestWithItemsDto toWithItemsDto(ItemRequest request, List<ItemDto> items) {
        return new ItemRequestWithItemsDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items
        );
    }
}