package ru.practicum.shareit.request.interfaces;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor_IdOrderByCreatedDesc(Long userId);

    Optional<ItemRequest> findItemRequestById(Long id);

    List<ItemRequest> findAllByRequestor_IdIsNot(Long userId, PageRequest pageRequest);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);





}
