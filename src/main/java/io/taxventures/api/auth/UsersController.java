package io.taxventures.api.auth;

import io.taxventures.api.common.ApiError;
import io.taxventures.exception.DuplicatedUsernameException;
import io.taxventures.service.token.TokenService;
import io.taxventures.service.user.User;
import io.taxventures.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UsersController {

    public UsersController(@Autowired UserService userService, @Autowired TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    private final UserService userService;
    private final TokenService tokenService;

    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<Object> register(@Valid @RequestBody UserCredentialsRequestBody requestBody){
        var request = new UserCredentialsRequestBody(requestBody.getUsername(), requestBody.getPassword());
        try {
            userService.registerUser(request.username, request.password);
            return ResponseEntity.ok().build();
        } catch (DuplicatedUsernameException ex) {
            return ResponseEntity.status(409).body(new ApiError(HttpStatus.CONFLICT, ex.getMessage()));
        }
    }

    @PostMapping("/api/login")
    @ResponseBody
    ResponseEntity<Object> login(@Valid @RequestBody UserCredentialsRequestBody requestBody) {
        UserCredentialsRequestBody request = new UserCredentialsRequestBody(requestBody.getUsername(), requestBody.getPassword());
        User user;
        try {
            user = userService.loadUserByUsername(request.getUsername());
        } catch (UsernameNotFoundException ex) {
            return invalidLoginResponse();
        }

        if (userService.isPasswordValid(user, request.getPassword())) {
            String token = tokenService.generateToken(user);
            return ResponseEntity.status(201).body(new AccessTokenResponse(token));
        }
        return invalidLoginResponse();
    }

    private ResponseEntity<Object> invalidLoginResponse()  {
        String invalidLoginMessage = "Combination of provided username and password do not exist!";
        return ResponseEntity.status(401).body(new ApiError(HttpStatus.UNAUTHORIZED, invalidLoginMessage));
    }
}
