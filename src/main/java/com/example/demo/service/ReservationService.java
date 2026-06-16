package com.example.demo.service;

import com.example.demo.dto.request.CreateReservationDTO;
import com.example.demo.dto.response.ReservationBookResponse;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.entity.Book;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationBook;
import com.example.demo.entity.enums.ReservationStatus;
import com.example.demo.entity.keys.ReservationBookId;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.BookRepository;
import com.example.demo.repository.bookshop.ReservationBookRepository;
import com.example.demo.repository.bookshop.ReservationRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ReservationBookRepository reservationBookRepository;
  private final BookRepository bookRepository;

  public Page<ReservationResponse> findAll(Pageable pageable) {
    return reservationRepository.findAll(pageable).map(this::toResponse);
  }

  public ReservationResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public ReservationResponse create(CreateReservationDTO input) {
    Reservation reservation =
        Reservation.builder()
            .reservationDate(
                input.getDate() != null
                    ? input.getDate().atStartOfDay()
                    : LocalDateTime.now())
            .status(ReservationStatus.PENDING)
            .build();
    reservation = reservationRepository.save(reservation);

    List<ReservationBook> books = new ArrayList<>();
    if (input.getBooks() != null) {
      for (var bookInput : input.getBooks()) {
        Book book =
            bookRepository
                .findById(bookInput.getBookId())
                .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                            "Book not found with id: " + bookInput.getBookId()));
        ReservationBookId id = new ReservationBookId(reservation.getId(), book.getId());
        ReservationBook rb = new ReservationBook(id, reservation, book, bookInput.getQuantity());
        books.add(reservationBookRepository.save(rb));
      }
    }
    reservation.setBooks(books);
    return toResponse(reservation);
  }

  @Transactional
  public ReservationResponse updateStatus(UUID id, ReservationStatus status) {
    Reservation reservation = getOrThrow(id);
    reservation.setStatus(status);
    return toResponse(reservationRepository.save(reservation));
  }

  @Transactional
  public void delete(UUID id) {
    Reservation reservation = getOrThrow(id);
    reservationBookRepository
        .findByReservationId(id)
        .forEach(reservationBookRepository::delete);
    reservationRepository.delete(reservation);
  }

  private Reservation getOrThrow(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
  }

  private ReservationResponse toResponse(Reservation reservation) {
    List<ReservationBook> books = reservation.getBooks();
    if (books == null) {
      books = reservationBookRepository.findByReservationId(reservation.getId());
    }
    return ReservationResponse.builder()
        .id(reservation.getId())
        .reservationDate(reservation.getReservationDate())
        .status(reservation.getStatus())
        .books(
            books.stream()
                .map(
                    rb ->
                        ReservationBookResponse.builder()
                            .bookId(rb.getBook().getId())
                            .bookTitle(rb.getBook().getTitle())
                            .quantity(rb.getQuantity())
                            .build())
                .toList())
        .build();
  }
}
