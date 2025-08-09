package com.github.fecrol.yasm.user.services;

import com.github.fecrol.yasm.comon.exceptions.BadRequestException;
import com.github.fecrol.yasm.comon.exceptions.ConflictException;
import com.github.fecrol.yasm.comon.exceptions.NotFoundException;
import com.github.fecrol.yasm.user.entities.User;
import com.github.fecrol.yasm.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void shouldReturnUserWhenGettingByExistingId() {
        // given
        User existingUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        // when
        userService.getUser(existingUser.getId());
        // then
        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(userRepository).findById(argumentCaptor.capture());
        UUID capturedUserId = argumentCaptor.getValue();
        assertThat(capturedUserId)
                .as("Check that the captured user ID matches the expected user ID")
                .isEqualTo(existingUser.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingByNotExistingId() {
        // give
        UUID nonExistingUserId = UUID.randomUUID();
        // then
        assertThatThrownBy(() -> userService.getUser(nonExistingUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id of " + nonExistingUserId + " not found");
    }

    @Test
    void shouldBeAbleToCreateNewUserWhenEmailAndHandleAreUnique() {
        // given
        User newUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        // when
        userRepository.save(newUser);
        // then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();
        assertThat(savedUser).isEqualTo(newUser);
    }

    @Test
    void shouldThrowConflictExceptionWhenUserWithEmailAlreadyExists() {
        // given
        User newUser = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(newUser));
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User with email " + newUser.getEmail() + " already exists");
    }

    @Test
    void shouldThrowConflictExceptionWhenUserWithHandleAlreadyExists() {
        // given
        User newUser = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findByHandle(anyString())).thenReturn(Optional.of(newUser));
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User with handle " + newUser.getHandle() + " already exists");
    }

    @Test
    void shouldThrowBadRequestExceptionWhenIllegalFieldsProvided() {
        // given
        User newUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Illegal fields provided: " + List.of("id"));
    }
}
