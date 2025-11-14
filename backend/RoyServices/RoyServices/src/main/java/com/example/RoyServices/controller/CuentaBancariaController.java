package com.example.RoyServices.controller;

import com.example.RoyServices.dto.CuentaBancariaDto;
import com.example.RoyServices.model.CuentaBancaria;
import com.example.RoyServices.service.CuentaBancariaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/Roy/api")
@RestController
@AllArgsConstructor
public class CuentaBancariaController {
    private final CuentaBancariaService cuentaBancariaService;

    // --- Mapeador de Entidad a DTO (Reutilizable) ---
    private CuentaBancariaDto mapToDto(CuentaBancaria cb) {
        return CuentaBancariaDto.builder()
                .idCuentaBancaria(cb.getIdCuentaBancaria())
                .banco(cb.getNombre())          // Mapea 'nombre' de Entidad a 'banco' de DTO
                .num_tarjeta(cb.getApellido())  // Mapea 'apellido' de Entidad a 'num_tarjeta' de DTO
                .idUsuario(cb.getIdUsuario())
                .build();
    }

    // --- Mapeador de DTO a Entidad (Reutilizable) ---
    private CuentaBancaria mapToEntity(CuentaBancariaDto dto) {
        return CuentaBancaria.builder()
                .nombre(dto.getBanco())
                .apellido(dto.getNum_tarjeta())
                .idUsuario(dto.getIdUsuario())
                .build();
    }


    @GetMapping("/cuentabancaria")
    public ResponseEntity<List<CuentaBancariaDto>> lista()
    {
        List<CuentaBancaria> cuentas = cuentaBancariaService.getAll();

        if(cuentas.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                cuentas.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/cuentabancaria/{id}")
    public ResponseEntity<CuentaBancariaDto> getById(@PathVariable Integer id)
    {
        CuentaBancaria cb = cuentaBancariaService.getById(id);

        if(cb == null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToDto(cb));
    }

    @PostMapping("/cuentabancaria")
    public ResponseEntity<CuentaBancariaDto> save(@RequestBody CuentaBancariaDto cuentaBancariaDto)
    {
        CuentaBancaria cb = mapToEntity(cuentaBancariaDto);

        CuentaBancaria savedCb = cuentaBancariaService.save(cb);

        return ResponseEntity.ok(mapToDto(savedCb));
    }

    @DeleteMapping("/cuentabancaria/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id)
    {
        cuentaBancariaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cuentabancaria/{id}")
    public ResponseEntity<CuentaBancariaDto> update(@PathVariable Integer id, @RequestBody CuentaBancariaDto cuentaBancariaDto)
    {
        CuentaBancaria cbToUpdate = mapToEntity(cuentaBancariaDto);

        CuentaBancaria updatedCb = cuentaBancariaService.update(id, cbToUpdate);

        return ResponseEntity.ok(mapToDto(updatedCb));
    }
}