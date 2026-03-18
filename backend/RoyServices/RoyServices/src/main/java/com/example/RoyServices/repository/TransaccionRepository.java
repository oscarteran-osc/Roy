package com.example.RoyServices.repository;

import com.example.RoyServices.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Integer> {

    List<Transaccion> findByIdSolicitud(Integer idSolicitud);

    List<Transaccion> findByEstatusPago(String estatusPago);
}