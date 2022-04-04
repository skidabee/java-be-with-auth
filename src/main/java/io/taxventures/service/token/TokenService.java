package io.taxventures.service.token;

import io.taxventures.service.user.User;
import io.taxventures.service.user.UserPrincipal;

public interface TokenService {
    String generateToken(User user);
    UserPrincipal parseToken(String token);
}
