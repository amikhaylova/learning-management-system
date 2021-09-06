package com.mikhaylova.lms.service;

import com.mikhaylova.lms.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface SecurityService {
    void changeAuthNameAndPassword(User user);

    String getCurrentPrincipalUsername();
}
