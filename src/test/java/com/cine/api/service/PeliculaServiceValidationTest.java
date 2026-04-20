package com.cine.api.service;

import com.cine.api.entity.Pelicula;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.service.exception.BusinessValidationException;
import com.cine.api.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeliculaServiceValidationTest {

    @Mock
    private PeliculaRepository peliculaRepository;

    @InjectMocks
    private PeliculaService peliculaService;

    @Test
    void guardar_cuandoTituloVacio_lanzaExcepcion() {
        Pelicula pelicula = buildPelicula("   ", 120, "PG-13", "Sci-Fi");

        assertThrows(BusinessValidationException.class, () -> peliculaService.guardar(pelicula));

        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    void guardar_cuandoDuracionInvalida_lanzaExcepcion() {
        Pelicula pelicula = buildPelicula("Dune", 0, "PG-13", "Sci-Fi");

        assertThrows(BusinessValidationException.class, () -> peliculaService.guardar(pelicula));

        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    void guardar_cuandoTituloDuplicado_lanzaExcepcion() {
        Pelicula pelicula = buildPelicula("Dune", 155, "PG-13", "Sci-Fi");
        Pelicula existente = buildPelicula("Dune", 140, "PG-13", "Sci-Fi");
        existente.setId(10L);

        when(peliculaRepository.findByTituloIgnoreCase("Dune")).thenReturn(Optional.of(existente));

        assertThrows(BusinessValidationException.class, () -> peliculaService.guardar(pelicula));

        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaExcepcion() {
        Pelicula peliculaNueva = buildPelicula("Dune", 155, "PG-13", "Sci-Fi");
        when(peliculaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> peliculaService.actualizar(99L, peliculaNueva));
    }

    @Test
    void actualizar_cuandoTituloDuplicadoEnOtroRegistro_lanzaExcepcion() {
        Pelicula existente = buildPelicula("Interstellar", 169, "PG-13", "Sci-Fi");
        existente.setId(1L);

        Pelicula otroRegistro = buildPelicula("Dune", 155, "PG-13", "Sci-Fi");
        otroRegistro.setId(2L);

        Pelicula cambios = buildPelicula("Dune", 170, "PG-13", "Sci-Fi");

        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(peliculaRepository.findByTituloIgnoreCase("Dune")).thenReturn(Optional.of(otroRegistro));

        assertThrows(BusinessValidationException.class, () -> peliculaService.actualizar(1L, cambios));

        verify(peliculaRepository, never()).save(any(Pelicula.class));
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(peliculaRepository.existsById(50L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> peliculaService.eliminar(50L));
    }

    @Test
    void actualizar_cuandoDatosValidos_actualizaCampos() {
        Pelicula existente = buildPelicula("Avatar", 162, "PG-13", "Sci-Fi");
        existente.setId(1L);

        Pelicula cambios = buildPelicula("Dune", 155, "PG-13", "Sci-Fi");

        when(peliculaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(peliculaRepository.findByTituloIgnoreCase("Dune")).thenReturn(Optional.empty());
        when(peliculaRepository.save(any(Pelicula.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pelicula actualizada = peliculaService.actualizar(1L, cambios);

        assertEquals("Dune", actualizada.getTitulo());
        assertEquals(155, actualizada.getDuracion());
        verify(peliculaRepository).save(existente);
    }

    private Pelicula buildPelicula(String titulo, Integer duracion, String clasificacion, String genero) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo(titulo);
        pelicula.setDuracion(duracion);
        pelicula.setClasificacion(clasificacion);
        pelicula.setGenero(genero);
        return pelicula;
    }
}
