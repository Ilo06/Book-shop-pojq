package com.example.demo.service;

import com.example.demo.dto.request.CreateUserDTO;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.ResourceConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public PageResponse<UserResponse> findAll(Pageable pageable) {
    return PageResponse.of(userRepository.findAll(pageable).map(this::toResponse));
  }

  public UserResponse findById(UUID id) {
    return toResponse(getOrThrow(id));
  }

  @Transactional
  public UserResponse create(CreateUserDTO input) {
    if (userRepository.findByUsername(input.getUsername()).isPresent()) {
      throw new ResourceConflictException(
          "User with username '" + input.getUsername() + "' already exists");
    }

    User user =
        User.builder()
            .username(input.getUsername())
            .password(passwordEncoder.encode(input.getPassword()))
            .role(input.getRole() != null ? input.getRole() : UserRole.USER)
            .build();

    return toResponse(userRepository.save(user));
  }

  @Transactional
  public void delete(UUID id) {
    User user = getOrThrow(id);
    userRepository.delete(user);
  }

  private User getOrThrow(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
  }

  private UserResponse toResponse(User user) {
    return UserResponse.from(user);
  }
}
