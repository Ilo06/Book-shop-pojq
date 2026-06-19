package com.example.demo.datastructure;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ListGrouperTest {

  ListGrouper<String> grouper = new ListGrouper<>();

  @Test
  void groupsListEvenly() {
    List<List<String>> result = grouper.apply(List.of("a", "b", "c", "d"), 2);

    assertEquals(2, result.size());
    assertEquals(List.of("a", "b"), result.get(0));
    assertEquals(List.of("c", "d"), result.get(1));
  }

  @Test
  void groupsWithRemainder() {
    List<List<String>> result = grouper.apply(List.of("a", "b", "c"), 2);

    assertEquals(2, result.size());
    assertEquals(List.of("a", "b"), result.get(0));
    assertEquals(List.of("c"), result.get(1));
  }

  @Test
  void groupSizeLargerThanList() {
    List<List<String>> result = grouper.apply(List.of("a", "b"), 10);

    assertEquals(1, result.size());
    assertEquals(List.of("a", "b"), result.get(0));
  }

  @Test
  void emptyList() {
    List<List<String>> result = grouper.apply(List.of(), 3);

    assertTrue(result.isEmpty());
  }

  @Test
  void singleElement() {
    List<List<String>> result = grouper.apply(List.of("a"), 1);

    assertEquals(1, result.size());
    assertEquals(List.of("a"), result.get(0));
  }
}
