package cl.triskeledu.catalogo.service;

import cl.triskeledu.catalogo.client.RecursoClient;
import cl.triskeledu.catalogo.dto.LibroResponse;
import cl.triskeledu.catalogo.mapper.LibroMapper;
import cl.triskeledu.catalogo.model.Libro;
import cl.triskeledu.catalogo.repository.CategoriaRepository;
import cl.triskeledu.catalogo.repository.LibroRepository;
import cl.triskeledu.common.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias didácticas para LibroService.
 *
 * Objetivo de la clase:
 * 1. ARRANGE: preparar datos y comportamiento de los mocks.
 * 2. ACT: ejecutar el método real de LibroService.
 * 3. ASSERT: comprobar el resultado esperado.
 *
 * Estas pruebas NO levantan PostgreSQL, Eureka ni Docker Compose.
 */
@ExtendWith(MockitoExtension.class)
class LibroServiceCoberturaTest {

    // Dependencias simuladas: permiten probar LibroService de forma aislada.
    @Mock
    private LibroRepository libroRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LibroMapper libroMapper;

    @Mock
    private RecursoClient recursoClient;

    // Objeto real que queremos probar; Mockito le inyecta los mocks anteriores.
    @InjectMocks
    private LibroService libroService;

    @Test
    void findById_deberiaRetornarLibro_cuandoExiste() {
        // ARRANGE: preparamos entidad, DTO esperado y respuestas de los mocks.
        Libro libro = crearLibro();
        LibroResponse esperado = crearRespuesta();

        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));
        when(libroMapper.toResponse(libro)).thenReturn(esperado);

        // ACT: ejecutamos el método real que queremos probar.
        LibroResponse resultado = libroService.findById(1L);

        // ASSERT: comprobamos que el resultado sea el esperado.
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Clean Code", resultado.getTitulo());

        // También verificamos que LibroService haya consultado el repositorio.
        verify(libroRepository).findById(1L);
        verify(libroMapper).toResponse(libro);
    }

    @Test
    void findById_deberiaLanzarExcepcion_cuandoNoExiste() {
        // ARRANGE: el repositorio simula que el ID solicitado no existe.
        when(libroRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT: verificamos que se lance la excepción de negocio esperada.
        assertThrows(EntityNotFoundException.class, () -> libroService.findById(99L));

        verify(libroRepository).findById(99L);
        verifyNoInteractions(libroMapper);
    }

    @Test
    void existsByIsbn_deberiaRetornarTrue_cuandoExiste() {
        // ARRANGE: definimos qué responderá el repositorio simulado.
        String isbn = "9780132350884";
        when(libroRepository.existsByIsbn(isbn)).thenReturn(true);

        // ACT: ejecutamos la regla real de LibroService.
        boolean resultado = libroService.existsByIsbn(isbn);

        // ASSERT: validamos el valor retornado.
        assertTrue(resultado);
        verify(libroRepository).existsByIsbn(isbn);
    }

    private Libro crearLibro() {
        return Libro.builder()
                .id(1L)
                .isbn("9780132350884")
                .titulo("Clean Code")
                .editorial("Prentice Hall")
                .anioPublicacion(2008)
                .autor("Robert C. Martin")
                .build();
    }

    private LibroResponse crearRespuesta() {
        LibroResponse response = new LibroResponse();
        response.setId(1L);
        response.setIsbn("9780132350884");
        response.setTitulo("Clean Code");
        response.setEditorial("Prentice Hall");
        response.setAnioPublicacion(2008);
        response.setAutor("Robert C. Martin");
        return response;
    }
}
