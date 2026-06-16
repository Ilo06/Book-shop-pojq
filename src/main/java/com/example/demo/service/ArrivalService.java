package com.example.demo.service;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.response.ArrivalBookResponse;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.entity.Arrival;
import com.example.demo.entity.ArrivalBook;
import com.example.demo.entity.Book;
import com.example.demo.entity.keys.ArrivalBookId;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.ArrivalBookRepository;
import com.example.demo.repository.bookshop.ArrivalRepository;
import com.example.demo.repository.bookshop.BookRepository;
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
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final ArrivalBookRepository arrivalBookRepository;
  private final BookRepository bookRepository;

  public Page<ArrivalResponse> findAll(Pageable pageable) {
    return arrivalRepository.findAll(pageable).map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public ArrivalResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public ArrivalResponse create(CreateArrivalDTO input) {
    Arrival arrival =
        Arrival.builder()
            .arrivalDate(input.getArrivalDate())
            .build();
    arrival = arrivalRepository.save(arrival);

    List<ArrivalBook> books = new ArrayList<>();
    if (input.getBooks() != null) {
      for (var bookInput : input.getBooks()) {
        Book book =
            bookRepository
                .findById(bookInput.getBookId())
                .orElseThrow(
                    () ->
                        new ResourceNotFoundException(
                            "Book not found with id: " + bookInput.getBookId()));
        ArrivalBookId id = new ArrivalBookId(arrival.getId(), book.getId());
        ArrivalBook ab = new ArrivalBook(id, arrival, book, bookInput.getQuantity());
        books.add(arrivalBookRepository.save(ab));
      }
    }
    arrival.setBooks(books);
    return toResponse(arrival);
  }

  @Transactional
  public void delete(UUID id) {
    Arrival arrival = getOrThrow(id);
    arrivalBookRepository
        .findByArrivalId(id)
        .forEach(arrivalBookRepository::delete);
    arrivalRepository.delete(arrival);
  }

  private Arrival getOrThrow(UUID id) {
    return arrivalRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Arrival not found with id: " + id));
  }

  private ArrivalResponse toResponse(Arrival arrival) {
    List<ArrivalBook> books = arrival.getBooks();
    if (books == null) {
      books = arrivalBookRepository.findByArrivalId(arrival.getId());
    }
    return ArrivalResponse.builder()
        .id(arrival.getId())
        .arrivalDate(arrival.getArrivalDate())
        .books(
            books.stream()
                .map(
                    ab ->
                        ArrivalBookResponse.builder()
                            .bookId(ab.getBook().getId())
                            .bookTitle(ab.getBook().getTitle())
                            .quantity(ab.getQuantity())
                            .build())
                .toList())
        .build();
  }
}
