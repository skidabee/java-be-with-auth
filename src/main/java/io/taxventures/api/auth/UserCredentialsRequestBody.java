package io.taxventures.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserCredentialsRequestBody {
    @NotNull(message = "should not be blank!") String username;
    @NotNull(message = "should not be blank!") String password;
}
