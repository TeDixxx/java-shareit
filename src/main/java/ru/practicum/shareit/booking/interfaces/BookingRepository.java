package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.Status;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking AS b " +
            " WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker.id = :userId )")
    Optional<Booking> findBookingByOwnerOrBooker(Long userId, Long bookingId);

//----- get by booker id

    //all
    Collection<Booking> findByBooker_IdOrderByStartDesc(Long id);

    //current
    Collection<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end);

    //future
    Collection<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now);

    //past
    Collection<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now);

    //waiting
    Collection<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long id, Status status);


    //----- get by owner id

    //current
    Collection<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start,
                                                                                     LocalDateTime end);

    //past
    Collection<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now);

    //future
    Collection<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now);

    //rejected
    Collection<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long id, Status status);

    //All
    Collection<Booking> findByItem_Owner_IdOrderByStartDesc(Long id);


    Collection<Booking> findAllByItemId(Long itemId);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);

    Booking findFirstByItem_IdAndBookerIdAndEndIsBeforeAndStatus(Long itemId,Long userId, LocalDateTime now, Status status);
}
