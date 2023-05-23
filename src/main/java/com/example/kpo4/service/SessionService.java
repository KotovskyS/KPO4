package com.example.kpo4.service;

import com.example.kpo4.model.SessionModel;
import com.example.kpo4.repo.SessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private final SessionRepo sessionRepository;

    @Autowired
    public SessionService(SessionRepo sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public SessionModel findBySessionToken(String token) {
        return sessionRepository.findBySessionToken(token);
    }

    public void save(SessionModel session) {
        sessionRepository.save(session);
    }
}
