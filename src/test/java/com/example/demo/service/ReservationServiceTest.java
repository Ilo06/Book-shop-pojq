package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.entity.Book;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationBook;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.ReservationBookRepository;
import com.example.demo.repository.bookshop.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;
  @Mock private ReservationBookRepository reservationBookRepository;
  @Mock private BookCopyRepository bookCopyRepository;

  @InjectMocks private ReservationService reservationService;

  @Test
  void findAll_returnsPagedReservations() {
    var reservation =
        Reservation.builder().id(UUID.randomUUID()).status(ReservationStatus.PENDING).build();
    var page = new PageImpl<>(List.of(reservation));
    when(reservationRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(reservationBookRepository.findByReservationId(reservation.getId())).thenReturn(List.of());

    var result = reservationService.findAll(PageRequest.of(0, 20));

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void findById_returnsReservation() {
    var id = UUID.randomUUID();
    var reservation = Reservation.builder().id(id).status(ReservationStatus.PENDING).build();
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
    when(reservationBookRepository.findByReservationId(id)).thenReturn(List.of());

    var result = reservationService.findById(id);

    assertEquals(ReservationStatus.PENDING, result.getStatus());
  }

  @Test
  void create_savesReservationWithBooks() {
    var bookCopyId = UUID.randomUUID();
    var book = Book.builder().id(UUID.randomUUID()).title("Test Book").build();
    var bookCopy = BookCopy.builder().id(bookCopyId).book(book).build();
    var input =
        new CreateReservationDTO(
            LocalDate.now(), List.of(new QuantifiedBookCopyDTO(bookCopyId, 2)));

    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(
            invocation -> {
              var r = invocation.getArgument(0, Reservation.class);
              return Reservation.builder()
                  .id(UUID.randomUUID())
                  .reservationDate(r.getReservationDate())
                  .status(r.getStatus())
                  .build();
            });
    when(reservationBookRepository.save(any(ReservationBook.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = reservationService.create(input);

    assertNotNull(result.getId());
    assertEquals(ReservationStatus.PENDING, result.getStatus());
  }

  @Test
  void updateStatus_changesStatus() {
    var id = UUID.randomUUID();
    var reservation = Reservation.builder().id(id).status(ReservationStatus.PENDING).build();
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(reservationBookRepository.findByReservationId(id)).thenReturn(List.of());

    var result = reservationService.updateStatus(id, ReservationStatus.CONFIRMED);

    assertEquals(ReservationStatus.CONFIRMED, result.getStatus());
  }

  @Test
  void delete_removesReservation() {
    var id = UUID.randomUUID();
    var reservation = Reservation.builder().id(id).build();
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
    when(reservationBookRepository.findByReservationId(id)).thenReturn(List.of());

    reservationService.delete(id);

    verify(reservationRepository).delete(reservation);
  }
}
