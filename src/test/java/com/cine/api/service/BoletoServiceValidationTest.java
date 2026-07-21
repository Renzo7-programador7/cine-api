package com.cine.api.service;

import com.cine.api.dto.ComprarBoletoRequest;
import com.cine.api.entity.Boleto;
import com.cine.api.entity.Funcion;
import com.cine.api.entity.Pelicula;
import com.cine.api.entity.Usuario;
import com.cine.api.repository.FuncionRepository;
import com.cine.api.repository.BoletoRepository;
import com.cine.api.repository.PeliculaRepository;
import com.cine.api.repository.UsuarioRepository;
import com.cine.api.service.exception.BusinessValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BoletoServiceValidationTest {

    @Autowired private BoletoService boletoService;
    @Autowired private BoletoRepository boletoRepository;
    @Autowired private FuncionRepository funcionRepository;
    @Autowired private PeliculaRepository peliculaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Test
    void comprar_datosValidos_derivaPrecioEstadoYComprador() {
        Usuario comprador = guardarUsuario("compra.valida@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 32.5, 50);

        Boleto boleto = boletoService.comprar(nuevaCompra(funcion.getId(), 12), comprador.getEmail());

        assertThat(boleto.getId()).isNotNull();
        assertThat(boleto.getPrecio()).isEqualTo(32.5);
        assertThat(boleto.getEstado()).isEqualTo("ACTIVO");
        assertThat(boleto.getUsuario().getId()).isEqualTo(comprador.getId());
    }

    @Test
    void comprar_asientoActivoDuplicado_rechazaSegundaCompra() {
        Usuario comprador = guardarUsuario("asiento.duplicado@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 30);
        ComprarBoletoRequest request = nuevaCompra(funcion.getId(), 8);
        boletoService.comprar(request, comprador.getEmail());

        assertThatThrownBy(() -> boletoService.comprar(request, comprador.getEmail()))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("ya esta ocupado");
    }

    @Test
    void comprar_asientoFueraDeCapacidad_rechazaCompra() {
        Usuario comprador = guardarUsuario("asiento.invalido@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 10);

        assertThatThrownBy(() -> boletoService.comprar(
                nuevaCompra(funcion.getId(), 11), comprador.getEmail()))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("entre 1 y 10");
    }

    @Test
    void comprar_funcionVencida_rechazaCompra() {
        Usuario comprador = guardarUsuario("funcion.vencida@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().minusDays(1), 20.0, 30);

        assertThatThrownBy(() -> boletoService.comprar(
                nuevaCompra(funcion.getId(), 5), comprador.getEmail()))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("funcion finalizada");
    }

    @Test
    void listarDelUsuario_noIncluyeBoletosDeOtroComprador() {
        Usuario propietario = guardarUsuario("historial.propietario@test.com");
        Usuario otro = guardarUsuario("historial.otro@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 30);
        boletoService.comprar(nuevaCompra(funcion.getId(), 5), propietario.getEmail());
        boletoService.comprar(nuevaCompra(funcion.getId(), 6), otro.getEmail());

        assertThat(boletoService.listarDelUsuario(propietario.getEmail()))
                .hasSize(1)
                .allMatch(boleto -> boleto.getUsuario().getId().equals(propietario.getId()));
    }

    @Test
    void consultarDisponibilidad_incluyeSoloAsientosActivos() {
        Usuario comprador = guardarUsuario("disponibilidad@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 20);
        boletoService.comprar(nuevaCompra(funcion.getId(), 3), comprador.getEmail());
        Boleto cancelado = boletoService.comprar(
                nuevaCompra(funcion.getId(), 4), comprador.getEmail());
        boletoService.cancelar(cancelado.getId(), comprador.getEmail(), false);

        var disponibilidad = boletoService.consultarDisponibilidad(funcion.getId());

        assertThat(disponibilidad.capacidad()).isEqualTo(20);
        assertThat(disponibilidad.asientosOcupados()).containsExactly(3);
    }

    @Test
    void cancelar_boletoPropioActivo_liberaElAsiento() {
        Usuario comprador = guardarUsuario("cancelacion.valida@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 30);
        ComprarBoletoRequest request = nuevaCompra(funcion.getId(), 8);
        Boleto boleto = boletoService.comprar(request, comprador.getEmail());

        Boleto cancelado = boletoService.cancelar(boleto.getId(), comprador.getEmail(), false);
        Boleto nuevaCompra = boletoService.comprar(request, comprador.getEmail());

        assertThat(cancelado.getEstado()).isEqualTo("CANCELADO");
        assertThat(nuevaCompra.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    void cancelar_boletoAjeno_rechazaOperacion() {
        Usuario propietario = guardarUsuario("cancelacion.propietario@test.com");
        Usuario otro = guardarUsuario("cancelacion.otro@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().plusDays(1), 20.0, 30);
        Boleto boleto = boletoService.comprar(
                nuevaCompra(funcion.getId(), 8), propietario.getEmail());

        assertThatThrownBy(() -> boletoService.cancelar(
                boleto.getId(), otro.getEmail(), false))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("otro usuario");
    }

    @Test
    void cancelar_despuesDeIniciarFuncion_rechazaOperacion() {
        Usuario comprador = guardarUsuario("cancelacion.vencida@test.com");
        Funcion funcion = guardarFuncion(LocalDate.now().minusDays(1), 20.0, 30);
        Boleto boleto = guardarBoletoDirecto(comprador, funcion, 8);

        assertThatThrownBy(() -> boletoService.cancelar(
                boleto.getId(), comprador.getEmail(), false))
                .isInstanceOf(BusinessValidationException.class)
                .hasMessageContaining("despues de iniciar");
    }

    private ComprarBoletoRequest nuevaCompra(Long funcionId, int asiento) {
        ComprarBoletoRequest request = new ComprarBoletoRequest();
        request.setFuncionId(funcionId);
        request.setAsiento(asiento);
        return request;
    }

    private Usuario guardarUsuario(String email) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Comprador de prueba");
        usuario.setEmail(email);
        usuario.setPassword("password-de-prueba");
        usuario.setRol("USER");
        return usuarioRepository.save(usuario);
    }

    private Funcion guardarFuncion(LocalDate fecha, double precio, int capacidad) {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("Pelicula para compra " + fecha);
        pelicula.setDuracion(120);
        pelicula.setClasificacion("PG");
        pelicula.setGenero("Drama");
        pelicula = peliculaRepository.save(pelicula);

        Funcion funcion = new Funcion();
        funcion.setFecha(fecha);
        funcion.setHora(LocalTime.of(20, 0));
        funcion.setPrecio(precio);
        funcion.setCapacidad(capacidad);
        funcion.setPelicula(pelicula);
        return funcionRepository.save(funcion);
    }

    private Boleto guardarBoletoDirecto(Usuario usuario, Funcion funcion, int asiento) {
        Boleto boleto = new Boleto();
        boleto.setPrecio(funcion.getPrecio());
        boleto.setEstado("ACTIVO");
        boleto.setAsiento(asiento);
        boleto.setUsuario(usuario);
        boleto.setFuncion(funcion);
        return boletoRepository.save(boleto);
    }
}
