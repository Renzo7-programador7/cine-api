package com.cine.api.service;

import com.cine.api.dto.ProgramarFuncionRequest;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.service.exception.BusinessValidationException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FuncionServiceValidationTest {

    @Autowired private FuncionService funcionService;
    @Autowired private PeliculaRepository peliculaRepository;

    @Test
    void guardar_datosValidos_programaFuncionConPeliculaPersistida() {
        Pelicula pelicula = guardarPelicula("Programacion valida");

        Funcion guardada = funcionService.programar(
                nuevaFuncion(pelicula.getId(), LocalDate.now().plusDays(1), LocalTime.of(19, 30)));

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getPelicula().getId()).isEqualTo(pelicula.getId());
    }

    @Test
    void guardar_peliculaInexistente_rechazaProgramacion() {
        ProgramarFuncionRequest request = nuevaFuncion(
                999999L, LocalDate.now().plusDays(1), LocalTime.of(19, 30));

        assertThatThrownBy(() -> funcionService.programar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pelicula no encontrada");
    }

    @Test
    void guardar_fechaHoraPasada_rechazaProgramacion() {
        Pelicula pelicula = guardarPelicula("Programacion pasada");
        ProgramarFuncionRequest request = nuevaFuncion(
                pelicula.getId(),
                LocalDate.now().minusDays(1),
                LocalTime.of(19, 30));

        assertThatThrownBy(() -> funcionService.programar(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("posteriores al momento actual");
    }

    @Test
    void guardar_programacionDuplicada_rechazaSegundaFuncion() {
        Pelicula pelicula = guardarPelicula("Programacion duplicada");
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime hora = LocalTime.of(19, 30);
        funcionService.programar(nuevaFuncion(pelicula.getId(), fecha, hora));

        assertThatThrownBy(() -> funcionService.programar(nuevaFuncion(pelicula.getId(), fecha, hora)))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("ya tiene una funcion programada");
    }

    @Test
    void guardar_precioInvalido_rechazaProgramacion() {
        Pelicula pelicula = guardarPelicula("Precio invalido");
        ProgramarFuncionRequest request = nuevaFuncion(
                pelicula.getId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(19, 30));
        request.setPrecio(0.0);

        assertThatThrownBy(() -> funcionService.programar(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("precio");
    }

    @Test
    void guardar_capacidadInvalida_rechazaProgramacion() {
        Pelicula pelicula = guardarPelicula("Capacidad invalida");
        ProgramarFuncionRequest request = nuevaFuncion(
                pelicula.getId(),
                LocalDate.now().plusDays(1),
                LocalTime.of(19, 30));
        request.setCapacidad(0);

        assertThatThrownBy(() -> funcionService.programar(request))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("capacidad");
    }

    private Pelicula guardarPelicula(String titulo) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo);
        pelicula.setDuracion(120);
        pelicula.setClasificacion("PG");
        pelicula.setGenero("Drama");
        return peliculaRepository.save(pelicula);
    }

    private ProgramarFuncionRequest nuevaFuncion(Long peliculaId, LocalDate fecha, LocalTime hora) {
        ProgramarFuncionRequest request = new ProgramarFuncionRequest();
        request.setFecha(fecha);
        request.setHora(hora);
        request.setPrecio(20.0);
        request.setCapacidad(100);
        request.setPeliculaId(peliculaId);
        return request;
    }
}
