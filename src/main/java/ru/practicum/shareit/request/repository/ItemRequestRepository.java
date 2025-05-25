package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository {

    ItemRequest save(ItemRequest request);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findAllByRequesterId(Long requesterId);

    List<ItemRequest> findAllExceptRequester(Long requesterId, int from, int size);

    void deleteById(Long id);
}