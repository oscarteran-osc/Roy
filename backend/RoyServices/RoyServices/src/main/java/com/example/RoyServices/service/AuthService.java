package com.example.RoyServices.service;

import com.example.RoyServices.dto.AuthResponse;
import com.example.RoyServices.dto.LoginRequest;
import com.example.RoyServices.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);
}

