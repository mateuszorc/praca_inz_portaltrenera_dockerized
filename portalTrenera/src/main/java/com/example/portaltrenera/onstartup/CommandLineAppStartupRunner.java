package com.example.portaltrenera.onstartup;

import com.example.portaltrenera.model.User;
import com.example.portaltrenera.model.UserRole;
import com.example.portaltrenera.repository.UserRepository;
import lombok.extern.java.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = LogManager.getLogger(CommandLineAppStartupRunner.class);

    @Autowired
    public CommandLineAppStartupRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(String... args) throws Exception {
        boolean trenerExists = userRepository.findById(1L).isPresent();
        if (!trenerExists) {
            String encodedPassword = passwordEncoder.encode("trener");
            User trener = new User("trener", "trener", "trener@trener.pl", encodedPassword, UserRole.ADMIN, false, true);
            userRepository.save(trener);
            LOGGER.info("Trener account has been created!");
        } else {
            LOGGER.info("Trener account already exists.");
        }

    }
}