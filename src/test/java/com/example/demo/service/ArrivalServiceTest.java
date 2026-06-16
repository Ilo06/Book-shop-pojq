package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.dto.request.CreateArrivalBookDTO;
import com.example.demo.dto.request.CreateArrivalDTO;
import com.example.demo.entity.Arrival;
import com.example.demo.entity.ArrivalBook;
import com.example.demo.entity.Book;
import com.example.demo.repository.bookshop.ArrivalBookRepository;
import com.example.demo.repository.bookshop.ArrivalRepository;
import com.example.demo.repository.bookshop.BookRepository;
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
class ArrivalServiceTest {

  @Mock private ArrivalRepository arrivalRepository;
  @Mock private ArrivalBookRepository arrivalBookRepository;
  @Mock private BookRepository bookRepository;

  @InjectMocks private ArrivalService arrivalService;

  @Test
  void findAll_returnsPagedArrivals() {
    var arrival = Arrival.builder().id(UUID.randomUUID()).arrivalDate(LocalDate.now()).build();
    var page = new PageImpl<>(List.of(arrival));
    when(arrivalRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(arrivalBookRepository.findByArrivalId(arrival.getId())).thenReturn(List.of());

    var result = arrivalService.findAll(PageRequest.of(0, 20));

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void findById_returnsArrival() {
    var id = UUID.randomUUID();
    var arrival = Arrival.builder().id(id).arrivalDate(LocalDate.now()).build();
    when(arrivalRepository.findById(id)).thenReturn(Optional.of(arrival));
    when(arrivalBookRepository.findByArrivalId(id)).thenReturn(List.of());

    var result = arrivalService.findById(id);

    assertEquals(id, result.getId());
  }

  @Test
  void create_savesArrivalWithBooks() {
    var bookId = UUID.randomUUID();
    var book = Book.builder().id(bookId).title("Test Book").build();
    var input = new CreateArrivalDTO(LocalDate.now(), List.of(new CreateArrivalBookDTO(bookId, 5)));

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
    when(arrivalRepository.save(any(Arrival.class)))
        .thenAnswer(
            invocation -> {
              var a = invocation.getArgument(0, Arrival.class);
              return Arrival.builder()
                  .id(UUID.randomUUID())
                  .arrivalDate(a.getArrivalDate())
                  .build();
            });
    when(arrivalBookRepository.save(any(ArrivalBook.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    var result = arrivalService.create(input);

    assertNotNull(result.getId());
    assertEquals(1, result.getBooks().size());
  }

  @Test
  void delete_removesArrival() {
    var id = UUID.randomUUID();
    var arrival = Arrival.builder().id(id).arrivalDate(LocalDate.now()).build();
    when(arrivalRepository.findById(id)).thenReturn(Optional.of(arrival));
    when(arrivalBookRepository.findByArrivalId(id)).thenReturn(List.of());

    arrivalService.delete(id);

    verify(arrivalRepository).delete(arrival);
  }
}
