package io.taxventures.service.user;

import io.taxventures.exception.DuplicatedUsernameException;
import io.taxventures.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.IdGenerator;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

public class UserServiceTest {

    private final UUID SOME_UUID = UUID.fromString("4249e08b-ea49-42c3-81a4-a3a26cdb1d2c");
    private final String SOME_RAW_PASSWORD = "pass";
    private final String SOME_ENCODED_PASSWORD = "BBB";
    private final String SOME_USERNAME = "user@exmaple.com";

    @Mock UserRepository repository;
    @Mock IdGenerator idGenerator;
    @Mock PasswordEncoder passwordEncoder;
    UserService userService;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(this.repository, idGenerator, passwordEncoder);
    }

    @Test
    void  userCreated_when_passwordIsProvided() throws DuplicatedUsernameException {
        Mockito.when(idGenerator.generateId()).thenReturn(SOME_UUID);
        Mockito.when(passwordEncoder.encode(SOME_RAW_PASSWORD)).thenReturn(SOME_ENCODED_PASSWORD);

        String username = "user";
        User expectedUser = new User(SOME_UUID.toString(), username, SOME_ENCODED_PASSWORD);
        Mockito.when(repository.existsByEmail(eq(username))).thenReturn(false);
        Mockito.when(repository.save(eq(expectedUser))).thenReturn(expectedUser);

        User actualUser = userService.registerUser(username, SOME_RAW_PASSWORD);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void userIsNotCreated_when_UsernameIsOccupied() {
        Mockito.when(repository.existsByEmail(eq(SOME_USERNAME))).thenReturn(true);

        assertThrows(DuplicatedUsernameException.class, () -> userService.registerUser(SOME_USERNAME, "password"));

        Mockito.verify(repository, never()).save(any());
    }

    @Test
    void user_isFoundByEmail_when_UserWithProvidedEmailExists() {
        User expectedUser = new User(SOME_UUID.toString(), SOME_USERNAME, SOME_ENCODED_PASSWORD);
        Mockito.when(repository.findByEmail(eq(SOME_USERNAME))).thenReturn(Optional.of(expectedUser));

        UserDetails actualUser = userService.loadUserByUsername(SOME_USERNAME);

        assertEquals(actualUser, expectedUser);
    }

    @Test
    void throwsUsernameNotFoundException_when_tryingToGetUser_ByNonExistingUsername() {
        Mockito.when(repository.findByEmail((SOME_USERNAME))).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(SOME_USERNAME));
    }
}
