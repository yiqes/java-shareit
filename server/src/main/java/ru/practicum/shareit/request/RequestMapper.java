package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@Component
public class RequestMapper {

    private UserMapper userMapper;
    private UserService userService;
    private ItemService itemService;

    @Autowired
    public RequestMapper(UserMapper userMapper, UserService userService, ItemService itemService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    public RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                userMapper.toUserDto(request.getRequestor()),
                request.getCreated(),
                itemService.getByRequestId(request.getId())
        );
    }

    public Request toRequest(RequestDto requestDto, Long requestorId, LocalDateTime created) {
        return new Request(
                null,
                requestDto.getDescription(),
                userService.findUserById(requestorId),
                created
        );
    }
}