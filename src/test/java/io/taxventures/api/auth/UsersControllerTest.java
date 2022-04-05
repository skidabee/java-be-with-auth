package io.taxventures.api.auth;

import io.taxventures.exception.DuplicatedUsernameException;
import io.taxventures.service.token.TokenService;
import io.taxventures.service.user.User;
import io.taxventures.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class UsersControllerTest {

    private static String SOME_USERNAME = "testusername";
    private static String SOME_PASSWORD = "testuserpass";
    private static String SOME_TOKEN = "token";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    TokenService tokenService;

    @Test
    public void userRegistrationIsPerformedSuccessfullyOnValidRequest() throws Exception {
        when(userService.registerUser(eq(SOME_USERNAME), eq(SOME_PASSWORD))).thenReturn(new User("SOME_ID", SOME_USERNAME, "s"));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getUserAuthRequest())
        ).andExpect(status().isOk());
    }

    @Test
    public void userRegistrationIsNotPossilble_when_emailIsOccupied() throws Exception {
        when(userService.registerUser(eq(SOME_USERNAME), eq(SOME_PASSWORD))).thenThrow(new DuplicatedUsernameException());

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getUserAuthRequest()))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("user with provided email already exists!"));
    }

    @Test
    public void returnsAccessTokenOnValidLoginRequest() throws Exception {
        User testUser = new User("s", "s", "testuserpass");
        when(userService.loadUserByUsername(eq(SOME_USERNAME))).thenReturn(testUser);
        when(tokenService.generateToken(eq(testUser))).thenReturn(SOME_TOKEN);
        when(userService.isPasswordValid(eq(testUser), eq("testuserpass")) ).thenReturn(true);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getUserAuthRequest()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(SOME_TOKEN));
    }

    @Test
    public void returnsUnauthorizedOnWrongPasswordRequest() throws Exception {
        User testUser = new User("s", "s", "testuserpass");
        when(userService.loadUserByUsername(eq("testusername"))).thenReturn(testUser);
        when(userService.isPasswordValid(eq(testUser), eq("testuserpass")) ).thenReturn(false);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getUserAuthRequest()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Combination of provided username and password do not exist!"));
    }

    @Test
    public void returnsUnauthorizedOnNonExistingUsername() throws Exception {
        when(userService.loadUserByUsername(eq(SOME_USERNAME))).thenThrow(new UsernameNotFoundException("not found!"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getUserAuthRequest()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Combination of provided username and password do not exist!"));
    }

    @Test
    public void returnsBadRequestOnRequestWithoutPassword() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getUserAuthWithoutPassword()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("password: should not be blank!"));
    }

    @Test
    public void returnsBadRequestOnRequestWithoutUsername() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getUserAuthWithoutUsername()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("password: should not be blank!"));
    }

    private String getUserAuthRequest(){
        return """
                {
                    "username": "testusername",
                    "password": "testuserpass"
                }
                """;
    }

    private String getUserAuthWithoutPassword(){
        return """
                {
                    "username": "testusername"
                }
                """;
    }

    private String getUserAuthWithoutUsername(){
        return """
                {
                    "username": "testusername"
                }
                """;
    }

}
