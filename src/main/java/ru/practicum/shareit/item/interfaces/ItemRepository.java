package ru.practicum.shareit.item.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

   Collection<Item> getItemsByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item AS i " +
            " WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%' )) " +
            " OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%' )) " +
            " AND i.available = true " )
    Collection<Item> search(@Param("search") String text);

}
