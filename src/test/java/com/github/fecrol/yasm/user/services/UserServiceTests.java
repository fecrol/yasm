package com.github.fecrol.yasm.user.services;

import com.github.fecrol.yasm.comon.exceptions.BadRequestException;
import com.github.fecrol.yasm.comon.exceptions.ConflictException;
import com.github.fecrol.yasm.comon.exceptions.NotFoundException;
import com.github.fecrol.yasm.user.entities.User;
import com.github.fecrol.yasm.user.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
@DisplayName("When interacting with the User Service")
@Tag("unit-test")
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("it should successfully retrieve a user on attempt to get user by existing ID")
    void itShouldReturnUserWhenGettingByExistingId() {
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
    @DisplayName("it should throw a Not Found Exception on attempt to get user by non existent ID")
    void itShouldThrowNotFoundExceptionWhenGettingByNotExistingId() {
        // give
        UUID nonExistingUserId = UUID.randomUUID();
        // then
        assertThatThrownBy(() -> userService.getUser(nonExistingUserId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id of " + nonExistingUserId + " not found");
    }

    @Test
    @DisplayName("it should be able to create a new user with unique email and handle")
    void itShouldBeAbleToCreateNewUserWhenEmailAndHandleAreUnique() {
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
    @DisplayName("it should throw a Bad Request Exception on attempt to create a new user with payload containing illegal fields")
    void itShouldThrowBadRequestExceptionWhenIllegalFieldsProvided() {
        // given
        User newUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Illegal fields provided: " + List.of("id"));
    }

    @Test
    @DisplayName("it should throw a Conflict Exception on attempt to create new user with email that already exists")
    void itShouldThrowConflictExceptionWhenUserWithEmailAlreadyExists() {
        // given
        User newUser = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(newUser));
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User with email " + newUser.getEmail() + " already exists");
    }

    @Test
    @DisplayName("it should throw a Conflict Exception on attempt to create new user with handle that already exists")
    void itShouldThrowConflictExceptionWhenUserWithHandleAlreadyExists() {
        // given
        User newUser = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findByHandle(anyString())).thenReturn(Optional.of(newUser));
        // then
        assertThatThrownBy(() -> userService.createNewUser(newUser))
                .isInstanceOf(ConflictException.class)
                .hasMessage("User with handle " + newUser.getHandle() + " already exists");
    }

    @Test
    @DisplayName("it should successfully update user details for user with existing ID")
    void itShouldSuccessfullyUpdateUserWithExistingId() {
        // given
        User existingUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
        // when
        existingUser.setEmail("new.email@fakemail.com");
        userService.updateExistingUser(existingUser.getId(), existingUser);
        // Then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(argumentCaptor.capture());
        User updatedUser = argumentCaptor.getValue();
        assertThat(updatedUser).isEqualTo(existingUser);
        assertThat(updatedUser.getEmail()).isEqualTo(existingUser.getEmail());
    }

    @Test
    @DisplayName("it should throw a Not Found Exception on attempt to update a user with non existent ID")
    void itShouldThrowBadRequestExceptionWhenUpdatingUserWithNonExistentId() {
        // given
        User user = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        // then
        assertThatThrownBy(() -> userService.updateExistingUser(user.getId(), user))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id of " + user.getId() + " not found");
    }

    @Test
    @DisplayName("it should successfully delete user details for user with existing ID")
    void itShouldSuccessfullyDeleteUserWithExistentId() {
        // given
        User existingUser = new User(UUID.randomUUID(), "some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
        // when
        userService.deleteExistingUser(existingUser.getId());
        // then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).delete(argumentCaptor.capture());
        User deletedUser = argumentCaptor.getValue();
        assertThat(deletedUser).isEqualTo(existingUser);
    }

    @Test
    @DisplayName("it should throw a Not Found Exception on attempt to delete a user with non existent ID")
    void itShouldThrowBadRequestExceptionWhenDeletingUserWithNonExistentId() {
        // given
        User user = new User("some.user@fakemail.com", "P@55w0rd123!", "some.fake.user");
        // then
        assertThatThrownBy(() -> userService.deleteExistingUser(user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with id of " + user.getId() + " not found");
    }
}
