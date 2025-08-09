package com.github.fecrol.yasm.unit.respositories;

import com.github.fecrol.yasm.user.entities.User;
import com.github.fecrol.yasm.user.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@DisplayName("When using the User Repository")
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("No user is returned for non existing email")
    void noUserIsReturnedForNonExistingEmail() {
        // given
        String nonExistingEmail = "fake.email@fakemail.com";
        // when
        Optional<User> user = userRepository.findByEmail(nonExistingEmail);
        // then
        assertThat(
                "Expected user with email " + nonExistingEmail + " to not be present",
                user.isPresent(), is(equalTo(false))
        );
    }

    @Test
    @DisplayName("User is returned for existing email")
    void userIsReturnedForExistingEmail() {
        // given
        User savedUser = userRepository.save(new User(
                "some.user@fakemail.com",
                "password",
                "some.user"
        ));
        // when
        Optional<User> user = userRepository.findByEmail(savedUser.getEmail());
        // then
        assertThat(
                "Expected user with email " + savedUser.getEmail() + " to be present",
                user.isPresent(), is(equalTo(true))
        );
    }

    @Test
    @DisplayName("No user is returned for non existing handle")
    void noUserIsReturnedForNonExistingHandle() {
        // given
        String nonExistingUserHandle = "user.with.fake.handle";
        // when
        Optional<User> user = userRepository.findByHandle(nonExistingUserHandle);
        // then
        assertThat(
                "Expected user with handle " + nonExistingUserHandle + " to not be present",
                user.isPresent(), is(equalTo(false))
        );
    }

    @Test
    @DisplayName("User is returned for existing handle")
    void userIsReturnedForExistingHandle() {
        // given
        User savedUser = userRepository.save(new User(
                "some.user@fakemail.com",
                "password",
                "some.user"
        ));
        // when
        Optional<User> user = userRepository.findByHandle(savedUser.getHandle());
        // then
        assertThat(
                "Expected user with handle " + savedUser.getHandle() + " to be present",
                user.isPresent(), is(equalTo(true))
        );
    }
}
