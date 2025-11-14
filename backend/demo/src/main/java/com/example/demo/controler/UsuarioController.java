package com.example.demo.controler;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario;
import com.example.demo.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // ¡Necesitas esta importación!
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/Roy/api")
@RestController
@AllArgsConstructor

public class UsuarioController {
    private final UsuarioService usuarioService;

    private List<UsuarioDto> usuarioDtos;

    public void loadList()
    {
        usuarioDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            usuarioDtos.add(
                    UsuarioDto.builder()
                            .id(i++)
                            .nombre("Nombre " + i)
                            .apellido("Apellido " + i)
                            .correo("Correo " + i)
                            .telefono("Telefono " + i)
                            .direccion("Direccion " + i)
                            .fecharegistro("Fecha de registro " + i)
                            .password("Password " + i)
                            .build()
            );
        }
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista(@RequestParam(name = "nombre", defaultValue = "", required = false) String nombre)
    {
        List<Usuario> usuarios = usuarioService.getAll( );
        if(usuarios == null || usuarios.isEmpty())
        {
            return ResponseEntity.notFound( ).build( );
        }

        return ResponseEntity
                .ok(
                        usuarios
                                .stream()
                                .filter(u -> nombre.isEmpty() || u.getNombre().equalsIgnoreCase(nombre) )
                                .map(u -> UsuarioDto.builder()
                                        .id(u.getId())
                                        .nombre(u.getNombre())
                                        .apellido(u.getApellido())
                                        .correo(u.getCorreo())
                                        .telefono(u.getTelefono())
                                        .direccion(u.getDomicilio())
                                        .fecharegistro(u.getFechaDeRegistro() != null ? u.getFechaDeRegistro().toString() : null)
                                        .password(u.getPassword())
                                        .build() )
                                .collect(Collectors.toList()));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto>getById(@PathVariable Integer id)
    {

        Usuario u = usuarioService.getById( id );

        if(u == null )
        {
            return  ResponseEntity.notFound( ).build( );
        }
        return ResponseEntity.ok(UsuarioDto.builder()
                .id( u.getId())
                .nombre( u.getNombre())
                .apellido(u.getApellido())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .direccion(u.getDomicilio())
                .fecharegistro(u.getFechaDeRegistro() != null ? u.getFechaDeRegistro().toString() : null)
                .password( u.getPassword())
                .build( ) );
    }

    @PostMapping( "/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto)
    {
        Usuario u = Usuario.
                builder().nombre( usuarioDto.getNombre() )
                .apellido( usuarioDto.getApellido() )
                .correo( usuarioDto.getCorreo() )
                .telefono( usuarioDto.getTelefono() )
                .domicilio(usuarioDto.getDireccion())
                .fechaDeRegistro(usuarioDto.getFecharegistro() != null ? LocalDate.parse(usuarioDto.getFecharegistro()) : null)
                .password( usuarioDto.getPassword() )
                .build( );
        usuarioService.save( u );

        return ResponseEntity.ok(UsuarioDto.builder()
                .id( u.getId())
                .nombre( u.getNombre())
                .apellido( u.getApellido() )
                .correo( u.getCorreo() )
                .telefono( u.getTelefono() )
                .direccion(u.getDomicilio())
                .fecharegistro(u.getFechaDeRegistro() != null ? u.getFechaDeRegistro().toString() : null)
                .password( u.getPassword())
                .build( ) );
    }

    @DeleteMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto> delete(@PathVariable Integer id)
    {
        usuarioService.delete( id );
        return ResponseEntity.noContent( ).build( );
    }

    @PutMapping( "/usuario/{id}")
    public ResponseEntity<UsuarioDto>update( @PathVariable Integer id, @RequestBody UsuarioDto usuarioDto)
    {
        Usuario aux = usuarioService.update( id, Usuario
                .builder( )
                .password( usuarioDto.getPassword( ) )
                .nombre( usuarioDto.getNombre( ) )
                .apellido( usuarioDto.getApellido( ) )
                .correo( usuarioDto.getCorreo( ) )
                .telefono(usuarioDto.getTelefono())
                .domicilio(usuarioDto.getDireccion())
                .fechaDeRegistro(usuarioDto.getFecharegistro() != null ? LocalDate.parse(usuarioDto.getFecharegistro()) : null)
                .build( ) );

        return ResponseEntity.ok(UsuarioDto.builder()
                .id( aux.getId( ) )
                .nombre( aux.getNombre( ) )
                .apellido( aux.getApellido( ) )
                .correo( aux.getCorreo( ) )
                .telefono( aux.getTelefono())
                .direccion(aux.getDomicilio())
                .fecharegistro( aux.getFechaDeRegistro() != null ? aux.getFechaDeRegistro().toString() : null)
                .password( aux.getPassword( ) )
                .build( ) );
    }
}
