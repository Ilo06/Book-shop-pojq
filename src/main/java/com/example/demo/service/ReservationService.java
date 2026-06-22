package com.example.demo.service;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.request.PatchReservationDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ReservationBookLine;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.BookCopy;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationBook;
import com.example.demo.entity.enums.BookStatus;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.entity.keys.ReservationBookId;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnprocessableEntityException;
import com.example.demo.repository.bookshop.BookCopyRepository;
import com.example.demo.repository.bookshop.ReservationBookRepository;
import com.example.demo.repository.bookshop.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ReservationBookRepository reservationBookRepository;
  private final BookCopyRepository bookCopyRepository;

  public List<ReservationResponse> findAll(ReservationStatus status) {
    List<Reservation> reservations =
        status != null
            ? reservationRepository
                .findByStatus(status, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
            : reservationRepository.findAll();
    return reservations.stream().map(this::toResponse).toList();
  }

  public ReservationResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public ReservationResponse create(CreateReservationDTO input) {
    input
        .getBooks()
        .forEach(
            dto -> {
              BookCopy copy =
                  bookCopyRepository
                      .findById(dto.getBookCopyId())
                      .orElseThrow(
                          () ->
                              new ResourceNotFoundException(
                                  "BookCopy not found with id: " + dto.getBookCopyId()));

              if (copy.getStatus() != BookStatus.AVAILABLE) {
                throw new UnprocessableEntityException(
                    "BookCopy with id: "
                        + dto.getBookCopyId()
                        + " is not available (current status: "
                        + copy.getStatus()
                        + ")");
              }
            });

    LocalDateTime reservationDate =
        input.getDate() != null ? input.getDate().atStartOfDay() : LocalDateTime.now();

    Reservation reservation =
        Reservation.builder()
            .reservationDate(reservationDate)
            .status(ReservationStatus.PENDING)
            .build();
    Reservation saved = reservationRepository.save(reservation);

    List<ReservationBook> books =
        input.getBooks().stream().map(dto -> createReservationBook(saved, dto)).toList();

    saved.setBooks(reservationBookRepository.saveAll(books));
    return toResponse(saved);
  }

  @Transactional
  public ReservationResponse patch(UUID id, PatchReservationDTO input) {
    Reservation reservation = getOrThrow(id);
    ReservationStatus oldStatus = reservation.getStatus();
    ReservationStatus newStatus = input.getStatus();

    reservation.setStatus(newStatus);
    reservationRepository.save(reservation);

    List<ReservationBook> books = reservationBookRepository.findByReservationId(id);

    if (newStatus == ReservationStatus.CONFIRMED && oldStatus == ReservationStatus.PENDING) {
      books.forEach(
          rb -> {
            BookCopy copy = rb.getBook();
            copy.setStatus(BookStatus.RESERVED);
            bookCopyRepository.save(copy);
          });
    } else if (newStatus == ReservationStatus.CANCELLED) {
      books.forEach(
          rb -> {
            BookCopy copy = rb.getBook();
            if (copy.getStatus() == BookStatus.RESERVED) {
              copy.setStatus(BookStatus.AVAILABLE);
              bookCopyRepository.save(copy);
            }
          });
    }

    reservation.setBooks(books);
    return toResponse(reservation);
  }

  @Transactional
  public void delete(UUID id) {
    Reservation reservation = getOrThrow(id);

    if (reservation.getStatus() == ReservationStatus.PENDING
        || reservation.getStatus() == ReservationStatus.CONFIRMED) {
      List<ReservationBook> books = reservationBookRepository.findByReservationId(id);
      books.forEach(
          rb -> {
            BookCopy copy = rb.getBook();
            if (copy.getStatus() == BookStatus.RESERVED) {
              copy.setStatus(BookStatus.AVAILABLE);
              bookCopyRepository.save(copy);
            }
          });
    }

    reservationRepository.delete(reservation);
  }

  private Reservation getOrThrow(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
  }

  private ReservationBook createReservationBook(
      Reservation reservation, QuantifiedBookCopyDTO dto) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(dto.getBookCopyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "BookCopy not found with id: " + dto.getBookCopyId()));

    ReservationBookId id = new ReservationBookId(reservation.getId(), bookCopy.getId());
    return new ReservationBook(id, reservation, bookCopy, dto.getQuantity());
  }

  private ReservationResponse toResponse(Reservation reservation) {
    List<ReservationBookLine> lines =
        reservation.getBooks() == null
            ? List.of()
            : reservation.getBooks().stream()
                .map(rb -> new ReservationBookLine(rb.getBook().getId(), rb.getQuantity()))
                .toList();

    return ReservationResponse.builder()
        .id(reservation.getId())
        .reservationDate(reservation.getReservationDate())
        .status(reservation.getStatus())
        .books(lines)
        .build();
  }
}
