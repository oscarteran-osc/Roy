package com.example.RoyServices.repository;

import com.example.RoyServices.model.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {
    Optional<TokenRecuperacion> findByToken(String token);
    void deleteByCorreo(String correo);
}
