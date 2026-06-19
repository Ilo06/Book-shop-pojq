package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.request.PatchReservationDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.ReservationBookRepository;
import com.example.demo.repository.bookshop.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

  @Mock ReservationRepository reservationRepository;
  @Mock ReservationBookRepository reservationBookRepository;
  @Mock BookCopyRepository bookCopyRepository;
  ReservationService service;
  Reservation reservation;
  BookCopy bookCopy;
  UUID id, bookCopyId;

  @BeforeEach
  void setUp() {
    service =
        new ReservationService(reservationRepository, reservationBookRepository, bookCopyRepository);
    id = UUID.randomUUID();
    bookCopyId = UUID.randomUUID();
    bookCopy = BookCopy.builder().id(bookCopyId).build();
    reservation =
        Reservation.builder()
            .id(id)
            .reservationDate(LocalDateTime.now())
            .status(ReservationStatus.PENDING)
            .build();
  }

  @Test
  void findAll_withStatus_returnsFiltered() {
    Page<Reservation> page = new PageImpl<>(List.of(reservation));
    when(reservationRepository.findByStatus(ReservationStatus.PENDING, PageRequest.of(0, Integer.MAX_VALUE)))
        .thenReturn(page);

    List<ReservationResponse> result = service.findAll(ReservationStatus.PENDING);

    assertEquals(1, result.size());
  }

  @Test
  void findAll_withNullStatus_returnsAll() {
    when(reservationRepository.findAll()).thenReturn(List.of(reservation));

    List<ReservationResponse> result = service.findAll(null);

    assertEquals(1, result.size());
  }

  @Test
  void findById_returnsReservation() {
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

    ReservationResponse result = service.findById(id);

    assertEquals(ReservationStatus.PENDING, result.getStatus());
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(reservationRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
  }

  @Test
  void create_savesReservation() {
    QuantifiedBookCopyDTO qty = new QuantifiedBookCopyDTO(bookCopyId, 2);
    CreateReservationDTO input = new CreateReservationDTO(LocalDate.of(2024, 7, 1), List.of(qty));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(reservationRepository.save(any())).thenReturn(reservation);
    when(reservationBookRepository.saveAll(any())).thenReturn(List.of());

    ReservationResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_withNullDate_usesNow() {
    QuantifiedBookCopyDTO qty = new QuantifiedBookCopyDTO(bookCopyId, 1);
    CreateReservationDTO input = new CreateReservationDTO(null, List.of(qty));
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(reservationRepository.save(any())).thenReturn(reservation);
    when(reservationBookRepository.saveAll(any())).thenReturn(List.of());

    ReservationResponse result = service.create(input);

    assertNotNull(result);
  }

  @Test
  void create_throwsWhenBookCopyNotFound() {
    QuantifiedBookCopyDTO qty = new QuantifiedBookCopyDTO(bookCopyId, 1);
    CreateReservationDTO input = new CreateReservationDTO(LocalDate.now(), List.of(qty));
    when(reservationRepository.save(any())).thenReturn(reservation);
    when(bookCopyRepository.findById(bookCopyId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.create(input));
  }

  @Test
  void patch_updatesStatus() {
    PatchReservationDTO input = new PatchReservationDTO(ReservationStatus.CONFIRMED);
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));
    when(reservationRepository.save(any())).thenReturn(reservation);

    ReservationResponse result = service.patch(id, input);

    assertNotNull(result);
  }

  @Test
  void patch_throwsWhenNotFound() {
    when(reservationRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.patch(id, new PatchReservationDTO(ReservationStatus.CONFIRMED)));
  }

  @Test
  void delete_removesReservation() {
    when(reservationRepository.findById(id)).thenReturn(Optional.of(reservation));

    service.delete(id);

    verify(reservationRepository).delete(reservation);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(reservationRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
  }

  @Test
  void toResponse_withNullBooks_returnsEmptyLines() {
    Reservation r = Reservation.builder().id(id).reservationDate(LocalDateTime.now()).status(ReservationStatus.PENDING).build();

    when(reservationRepository.findAll()).thenReturn(List.of(r));

    List<ReservationResponse> result = service.findAll(null);

    assertTrue(result.get(0).getBooks().isEmpty());
  }
}
