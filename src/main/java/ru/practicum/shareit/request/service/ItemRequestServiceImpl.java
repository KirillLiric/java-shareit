package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequest create(ItemRequest request, Long requesterId) {
        validateRequest(request);
        request.setRequester(userService.getById(requesterId));
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public ItemRequest getById(Long requestId, Long userId) {
        userService.getById(userId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));
    }

    @Override
    public List<ItemRequest> getAllByRequester(Long requesterId) {
        userService.getById(requesterId);
        return requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
    }

    @Override
    public List<ItemRequest> getAllExceptRequester(Long requesterId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректные параметры пагинации");
        }
        userService.getById(requesterId);
        PageRequest page = PageRequest.of(from / size, size);
        return requestRepository.findByRequesterIdNotOrderByCreatedDesc(requesterId, page);
    }

    @Override
    @Transactional
    public void delete(Long requestId, Long userId) {
        ItemRequest request = getById(requestId, userId);
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Удалять запрос может только его создатель");
        }
        requestRepository.deleteById(requestId);
    }

    private void validateRequest(ItemRequest request) {
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }
    }
}