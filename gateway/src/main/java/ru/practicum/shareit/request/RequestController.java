package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class RequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final RequestClient requestClient;

    @ResponseBody
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid RequestDto requestDto,
                                         @RequestHeader(USER_ID) Long requestorId) {
        log.info("Получен POST-запрос к эндпоинту: '/requests' " +
                "на создание запроса вещи от пользователя с ID={}", requestorId);
        return requestClient.create(requestDto, requestorId);
    }

    @GetMapping("/{request-id}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("request-id") Long itemRequestId,
                                                     @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запроса с ID={}", itemRequestId);
        return requestClient.getItemRequestById(userId, itemRequestId);
    }


    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запросов пользователя ID={}",
                userId);
        return requestClient.getOwnItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) Long userId,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     Integer from,
                                                     @RequestParam(required = false) Integer size) {
        log.info("Получен GET-запрос к эндпоинту: '/requests/all' от пользователя с ID={} на получение всех запросов",
                userId);
        return requestClient.getAllItemRequests(userId, from, size);
    }
}