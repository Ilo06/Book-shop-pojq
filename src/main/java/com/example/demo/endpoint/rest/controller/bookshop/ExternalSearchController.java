package com.example.demo.endpoint.rest.controller.bookshop;

import com.example.demo.dto.response.ExternalSearchResponse;
import com.example.demo.exception.BadGatewayException;
import com.example.demo.service.ExternalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external-search")
@RequiredArgsConstructor
public class ExternalSearchController {

  private final ExternalSearchService externalSearchService;

  @GetMapping(params = "isbn")
  public ResponseEntity<ExternalSearchResponse> findBookByISBN(@RequestParam String isbn)
      throws BadGatewayException {
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(externalSearchService.findBookByISBN(isbn));
  }
}
