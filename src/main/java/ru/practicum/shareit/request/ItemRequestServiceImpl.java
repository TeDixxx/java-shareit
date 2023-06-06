package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.interfaces.ItemRequestRepository;
import ru.practicum.shareit.request.interfaces.ItemRequestService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.item.interfaces.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    @Autowired
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemMapper itemMapper;


    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {

        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        itemRequestDto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, user);
        itemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getForOwner(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);

        if (itemRequestList.isEmpty()) {
            return Collections.emptyList();
        }

        List<ItemRequestDto> itemRequestDtoList = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());

        List<Long> ids = itemRequestDtoList.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());


        List<Item> items = itemRepository.findAllByRequest_IdIn(ids);

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            List<Item> values = items.stream().
                    filter(item -> item.getRequest().getId()
                            .equals(itemRequestDto.getId())).collect(Collectors.toList());

            if (!values.isEmpty()) {
                List<ItemDto> itemDtos = values.stream()
                        .map(itemMapper::toItemDto)
                        .collect(Collectors.toList());

                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {

        if (from < 0 || size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect params");
        }
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found"));

        PageRequest pageRequest = PageRequest.of(from == 0 ? 0 : (from / size), size, Sort.Direction.DESC, "created");

        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestor_IdIsNot(userId, pageRequest);

        List<ItemRequestDto> itemRequestDtoList = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());

        List<Long> ids = itemRequestDtoList.stream()
                .map(ItemRequestDto::getId).collect(Collectors.toList());

        List<Item> items = itemRepository.findAllByRequest_IdIn(ids);

        for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
            List<Item> itemList = items.stream()
                    .filter(item -> item.getRequest().getId().equals(itemRequestDto.getId())).collect(Collectors.toList());

            if (!itemList.isEmpty()) {
                List<ItemDto> itemDtos = itemList.stream()
                        .map(itemMapper::toItemDto).collect(Collectors.toList());
                itemRequestDto.setItems(itemDtos);
            }
        }
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findItemRequestById(requestId).orElseThrow(()
                -> new ItemRequestNotFoundException("Request not found"));

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        List<Item> items = itemRepository.findAllByRequest_Id(itemRequestDto.getId());

        List<ItemDto> itemDtoList = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());

        itemRequestDto.setItems(itemDtoList);

        return itemRequestDto;

    }
}
