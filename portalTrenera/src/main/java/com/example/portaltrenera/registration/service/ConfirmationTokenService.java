package com.example.portaltrenera.registration.service;

import com.example.portaltrenera.registration.repository.ConfirmationTokenRepository;
import com.example.portaltrenera.registration.token.ConfirmationToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenService(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public void saveConfirmationToken(ConfirmationToken token) {
        this.confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return this.confirmationTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return this.confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
