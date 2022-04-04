package io.taxventures.api.auth;

import io.taxventures.api.common.ApiError;
import io.taxventures.exception.DuplicatedUsernameException;
import io.taxventures.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UsersController {

    public UsersController(@Autowired UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    @ResponseBody
    ResponseEntity<Object> register(@Valid @RequestBody UserCredentialsRequestBody requestBody){
        var request = new UserCredentialsRequestBody(requestBody.username(), requestBody.password());
        try {
            userService.registerUser(request.username(), request.password());
            return ResponseEntity.ok().build();
        } catch (DuplicatedUsernameException ex) {
            return ResponseEntity.status(409).body(new ApiError(HttpStatus.CONFLICT, ex.getMessage()));
        }
    }
}
