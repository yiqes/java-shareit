package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {

    RequestDto saveRequest(RequestDto requestDto, Long requestorId, LocalDateTime created);

    RequestDto getRequestById(Long requestId, Long userId);

    List<RequestDto> getAllRequests(Long userId, Integer from, Integer size);

    List<RequestDto> getOwnRequests(Long requestorId);
}
