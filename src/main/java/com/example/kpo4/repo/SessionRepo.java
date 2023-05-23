package com.example.kpo4.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.kpo4.model.SessionModel;

public interface SessionRepo extends JpaRepository<SessionModel, Long> {
    SessionModel findBySessionToken(String sessionToken);
}