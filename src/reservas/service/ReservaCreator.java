package reservas.service;

import reservas.config.AppConfig;

import java.io.*;
import java.util.*;

/**
 * Genera archivos de reservas: aleatorias o con errores forzados.
 *
 * <p>Los asientos se consumen sin repetición a lo largo de la sesión;
 * nombres, clases y destinos se eligen aleatoriamente sin restricción.
 */
public class ReservaCreator {

    // ── Datos de referencia ───────────────────────────────────────────────

    /** Asientos disponibles para asignar (se reducen con cada reserva). */
    private final List<String> asientosDisponibles = new ArrayList<>();

    /** Lista completa e inmutable de asientos — usada solo para validación. */
    private final List<String> asientosValidos;

    private final List<String> nombres   = new ArrayList<>();
    private final List<String> clases    = new ArrayList<>();
    private final List<String> destinos  = new ArrayList<>();

    private final Random random = new Random();

    // ── Constructor ───────────────────────────────────────────────────────

    public ReservaCreator() {
        List<String> todosAsientos = generarAsientos();
        asientosDisponibles.addAll(todosAsientos);
        asientosValidos = Collections.unmodifiableList(new ArrayList<>(todosAsientos));

        nombres.addAll(Arrays.asList("Alex", "Marina", "Miguel Angel", "Pau"));
        clases.addAll(Arrays.asList("Economy", "Business"));
        destinos.addAll(Arrays.asList(
            "Kyoto", "Machu Picchu", "París", "Santorini", "Nueva York",
            "Bali", "Roma", "Reykjavík", "Ciudad del Cabo", "Sydney"
        ));
    }

    private static List<String> generarAsientos() {
        List<String> lista = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            lista.add(i + "A");
            lista.add(i + "B");
            lista.add(i + "C");
        }
        return lista;
    }

    // ── Getters (listas inmutables para validación) ───────────────────────

    /** Lista completa de asientos válidos (independiente de los usados). */
    public List<String> getAsientosValidos()  { return asientosValidos; }
    public List<String> getNombres()           { return Collections.unmodifiableList(nombres); }
    public List<String> getClases()            { return Collections.unmodifiableList(clases); }
    public List<String> getDestinos()          { return Collections.unmodifiableList(destinos); }

    // ── API pública ───────────────────────────────────────────────────────

    /**
     * Genera {@link AppConfig#RESERVAS_POR_LOTE} reservas aleatorias y las
     * añade al archivo indicado.
     *
     * @param rutaArchivo ruta del archivo de salida
     * @param conDestino  {@code true} para incluir el campo destino
     */
    public void crear(String rutaArchivo, boolean conDestino) {
        if (asientosDisponibles.isEmpty()) {
            System.out.println("No quedan asientos disponibles.");
            return;
        }

        for (int i = 0; i < AppConfig.RESERVAS_POR_LOTE; i++) {
            if (asientosDisponibles.isEmpty()) break;

            String asiento = extraerAleatorio(asientosDisponibles);
            String nombre  = elegirAleatorio(nombres);
            String clase   = elegirAleatorio(clases);

            StringBuilder sb = new StringBuilder()
                .append(asiento).append(AppConfig.SEPARADOR)
                .append(nombre).append(AppConfig.SEPARADOR)
                .append(clase);

            if (conDestino) {
                sb.append(AppConfig.SEPARADOR).append(elegirAleatorio(destinos));
            }

            escribirLinea(rutaArchivo, sb.toString());
        }
    }

    /**
     * Genera el archivo de prueba {@code reservas_maestro_con_errores.txt}
     * con entradas que contienen distintos tipos de errores.
     */
    public void crearConErrores() {
        String[] lineas = {
            "1A, Alex, Economy, Nueva York",   // válida
            "3A, hola, Economy, Nueva York",   // nombre inválido
            "1B, Alex, Nueva York"             // faltan campos
        };
        for (String linea : lineas) {
            escribirLinea(AppConfig.FILE_MAESTRO_ERRORES, linea);
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────

    /** Extrae un elemento aleatorio de la lista (lo elimina de ella). */
    private String extraerAleatorio(List<String> lista) {
        int idx = random.nextInt(lista.size());
        String elemento = lista.get(idx);
        lista.remove(idx);
        return elemento;
    }

    /** Elige un elemento aleatorio sin eliminarlo de la lista. */
    private String elegirAleatorio(List<String> lista) {
        return lista.get(random.nextInt(lista.size()));
    }

    /** Añade una línea al archivo indicado (append). */
    private void escribirLinea(String rutaArchivo, String contenido) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(rutaArchivo, true))) {
            bw.write(contenido);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en " + rutaArchivo + ": " + e.getMessage());
        }
    }
}
