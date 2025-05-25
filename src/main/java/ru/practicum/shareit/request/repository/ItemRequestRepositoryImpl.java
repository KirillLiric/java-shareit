package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            request.setId(idCounter.getAndIncrement());
        }
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public List<ItemRequest> findAllByRequesterId(Long requesterId) {
        return requests.values().stream()
                .filter(request -> request.getRequester().getId().equals(requesterId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequest> findAllExceptRequester(Long requesterId, int from, int size) {
        return requests.values().stream()
                .filter(request -> !request.getRequester().getId().equals(requesterId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        requests.remove(id);
    }

}