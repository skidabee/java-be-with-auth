package io.taxventures.service.user;

import io.taxventures.exception.DuplicatedUsernameException;
import io.taxventures.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Autowired UserRepository userRepository,
                       @Autowired IdGenerator idGenerator,
                       @Autowired @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with provided email cannot be found."));
    }

    public User registerUser(String username, String password) throws DuplicatedUsernameException {
        if (userRepository.existsByEmail(username)) {
            throw new DuplicatedUsernameException();
        }
        String encryptedPassword = passwordEncoder.encode(password);
        return userRepository.save(toUser(username, encryptedPassword));
    }

    public boolean isPasswordValid(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.password);
    }

    User toUser(String username, String encryptedPassword)  {
        return new User(idGenerator.generateId().toString(), username, encryptedPassword);
    }
}
