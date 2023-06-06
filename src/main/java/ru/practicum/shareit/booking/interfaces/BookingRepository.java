package ru.practicum.shareit.booking.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.Status;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking AS b " +
            " WHERE b.id = :bookingId AND (b.item.owner.id = :userId OR b.booker.id = :userId )")
    Optional<Booking> findBookingByOwnerOrBooker(Long userId, Long bookingId);

//----- get by booker id

    //all
    List<Booking> findByBooker_IdOrderByStartDesc(Long id, Pageable pageable);

    //current
    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    //future
    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now , Pageable pageable); //???

    //past
    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now , Pageable pageable);

    //waiting
    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long id, Status status , Pageable pageable);


    //----- get by owner id

    //current
    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime start,
                                                                                     LocalDateTime end , Pageable pageable);

    //past
    List<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now , Pageable pageable);

    //future
    List<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now , Pageable pageable);

    //rejected
    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long id, Status status , Pageable pageable); //???

    //All
    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long id , Pageable pageable);


    Collection<Booking> findAllByItemId(Long itemId);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status, LocalDateTime now);

    Booking findFirstByItem_IdAndBookerIdAndEndIsBeforeAndStatus(Long itemId,Long userId, LocalDateTime now, Status status);
}
