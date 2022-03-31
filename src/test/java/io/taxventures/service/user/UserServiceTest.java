package io.taxventures.service.user;

import exception.DuplicatedUsernameException;
import io.taxventures.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.IdGenerator;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

public class UserServiceTest {

    private final UUID SOME_UUID = UUID.fromString("4249e08b-ea49-42c3-81a4-a3a26cdb1d2c");
    private final String SOME_RAW_PASSWORD = "pass";
    private final String SOME_ENCODED_PASSWORD = "BBB";

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

}
