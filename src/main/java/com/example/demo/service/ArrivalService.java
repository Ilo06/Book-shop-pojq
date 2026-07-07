package com.example.demo.service;

import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.dto.request.QuantifiedBookCopyDTO;
import com.example.demo.dto.response.ArrivalBookLine;
import com.example.demo.dto.response.ArrivalResponse;
import com.example.demo.entity.Arrival;
import com.example.demo.entity.ArrivalBook;
import com.example.demo.entity.BookCopy;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.bookshop.ArrivalBookRepository;
import com.example.demo.repository.bookshop.ArrivalRepository;
import com.example.demo.repository.bookshop.BookCopyRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final ArrivalBookRepository arrivalBookRepository;
  private final BookCopyRepository bookCopyRepository;

  public List<ArrivalResponse> findAll() {
    return arrivalRepository.findAll().stream().map(this::toResponse).toList();
  }

  public ArrivalResponse findById(UUID id) {
    Arrival arrival =
        arrivalRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Arrival not found with id: " + id));
    return toResponse(arrival);
  }

  @Transactional
  public ArrivalResponse create(CreateArrivalDTO input) {
    Arrival arrival = Arrival.builder().arrivalDateTime(input.getArrivalDateTime()).build();

    if (bookCopyRepository.findAllById(input.getBooks()
            .stream()
            .map(QuantifiedBookCopyDTO::getBookCopyId).toList())
            .size() != input.getBooks().size()) {
      throw new ResourceNotFoundException("Some Book copy not found");
    }

    Arrival savedArrival = arrivalRepository.save(arrival);

    List<ArrivalBook> arrivalBooks =
        input.getBooks().stream().map(dto -> createArrivalBook(savedArrival, dto)).toList();

    savedArrival.setBooks(arrivalBookRepository.saveAll(arrivalBooks));
    return toResponse(savedArrival);
  }

  private ArrivalBook createArrivalBook(Arrival arrival, QuantifiedBookCopyDTO dto) {
    BookCopy bookCopy =
        bookCopyRepository
            .findById(dto.getBookCopyId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "BookCopy not found with id: " + dto.getBookCopyId()));

    return new ArrivalBook(null, arrival,  bookCopy, dto.getQuantity());
  }

  private ArrivalResponse toResponse(Arrival arrival) {
    List<ArrivalBookLine> lines =
        arrival.getBooks() == null
            ? List.of()
            : arrival.getBooks().stream()
                .map(ab -> new ArrivalBookLine(ab.getBookCopy().getId(), ab.getQuantity()))
                .toList();

    return ArrivalResponse.builder()
        .id(arrival.getId())
        .arrivalDateTime(arrival.getArrivalDateTime())
        .books(lines)
        .build();
  }
}
