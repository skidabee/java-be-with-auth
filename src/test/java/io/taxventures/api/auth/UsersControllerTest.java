package io.taxventures.api.auth;

import io.taxventures.service.token.TokenService;
import io.taxventures.service.user.User;
import io.taxventures.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UsersControllerTest {

    private static String SOME_USERNAME = "testusername";
    private static String SOME_PASSWORD = "testuserpass";

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

    private String getUserAuthRequest(){
        return """
                {
                    "username": "testusername",
                    "password": "testuserpass"
                }
                """;
    }

}
