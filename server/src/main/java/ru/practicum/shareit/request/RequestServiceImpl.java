package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.service.ValidationService;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final ValidationService validationService;
    private final RequestMapper requestMapper;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, ValidationService validationService,
                              RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.validationService = validationService;
        this.requestMapper = requestMapper;
    }

    @Override
    public RequestDto saveRequest(RequestDto requestDto, Long requestorId, LocalDateTime created) {
        Request request = requestMapper.toRequest(requestDto, requestorId, created);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto getRequestById(Long requestId, Long userId) {
        validationService.isExistUser(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() ->  new RequestNotFoundException("Запрос с id = " + requestId + " не найден!"));
        return requestMapper.toRequestDto(request);
    }

    @Override
    public List<RequestDto> getOwnRequests(Long requestorId) {
        validationService.isExistUser(requestorId);
        return requestRepository.findAllByRequestorId(requestorId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(requestMapper::toRequestDto)
                .collect(toList());
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        validationService.isExistUser(userId);
        List<RequestDto> listItemRequestDto = new ArrayList<>();
        Pageable pageable;
        Page<Request> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            List<Request> listItemRequest = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
            listItemRequestDto
                    .addAll(listItemRequest.stream().skip(from).map(requestMapper::toRequestDto).collect(toList()));
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = requestRepository.findAllByRequestorIdNot(userId, pageable);
                listItemRequestDto.addAll(page.stream().map(requestMapper::toRequestDto).collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemRequestDto = listItemRequestDto.stream().limit(size).collect(toList());
        }
        return listItemRequestDto;
    }
}
