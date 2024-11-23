package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;
    private static final String header = "X-Sharer-User-Id";

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @ResponseBody
    @PostMapping
    public RequestDto createRequest(@RequestBody RequestDto requestDto,
                                                    @RequestHeader(header) Long requestorId) {
        return requestService.saveRequest(requestDto, requestorId, LocalDateTime.now());
    }

    @GetMapping("/{request-id}")
    public RequestDto getRequest(@PathVariable("request-id") Long requestId,
                                                 @RequestHeader(header) Long userId) {
        return requestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestHeader(header) Long userId,
                                           @RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(required = false) Integer size) {
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping
    public List<RequestDto> getOwnRequests(@RequestHeader(header) Long userId) {
        return requestService.getOwnRequests(userId);
    }
}
