package com.vishwanath.userservice.service;

import com.vishwanath.userservice.model.User;

public interface AuthenticationService {
    String register(User request);
    String authenticate(User request);
}
