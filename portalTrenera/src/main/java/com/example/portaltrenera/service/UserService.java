package com.example.portaltrenera.service;

import com.example.portaltrenera.model.User;
import com.example.portaltrenera.registration.service.ConfirmationTokenService;
import com.example.portaltrenera.registration.token.ConfirmationToken;
import com.example.portaltrenera.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       ConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }

    public List<User> getUsers() {
        return userRepository.findAllUsers();
    }

    @Transactional
    public User getSingleUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with id: " + id + " does not exist"));
    }

    @Transactional
    public User editUser(Long id, User user) {
        User editedUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User with id: " + user.getId() + " does not exist"));
        editedUser.setFirstName(user.getFirstName());
        editedUser.setLastName(user.getLastName());
        if (!user.getPassword().equals("")) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            editedUser.setPassword(encodedPassword);
        }
        return editedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public String registerUser(User user) {
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        boolean userConfirmed = userRepository.findByEmailIfEnabled(user.getEmail()).isPresent();
        if (userExists && userConfirmed) {
            LOGGER.info("Tried to register user by email that is already taken.");
            throw new IllegalStateException("Email already taken");
        } else {
            String token;
            ConfirmationToken confirmationToken;
            if (userExists && !userConfirmed) {
                User existingUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> {
                    return new IllegalStateException("Could not find existing but not confirmed user: "
                            + user.getEmail());
                });
                token = UUID.randomUUID().toString();
                confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusHours(24L), existingUser);
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                LOGGER.info("Resent confirmation email to: " + user.getEmail());
                return token;
            } else {
                String encodedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                this.userRepository.save(user);
                token = UUID.randomUUID().toString();
                confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusHours(24L), user);
                this.confirmationTokenService.saveConfirmationToken(confirmationToken);
                LOGGER.info("Registered user: " + user.getEmail());
                return token;
            }
        }
    }

    public int enableUser(String email) {
        return this.userRepository.enableUser(email);
    }
}
