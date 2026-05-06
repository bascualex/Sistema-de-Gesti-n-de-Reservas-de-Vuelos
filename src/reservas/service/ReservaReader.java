package reservas.service;

import reservas.config.AppConfig;
import reservas.util.ErrorLogger;
import reservas.util.Validator;

import java.io.*;
import java.util.*;

/**
 * Lee y procesa archivos de reservas.
 *
 * <ul>
 *   <li><b>reservas.txt</b> — imprime líneas, cuenta total y Business.</li>
 *   <li><b>reservas_maestro*.txt</b> — valida cada línea, loguea errores,
 *       genera archivos por destino sin duplicados y muestra un resumen.</li>
 * </ul>
 */
public class ReservaReader {

    private final Validator    validator;
    private final ErrorLogger  errorLogger = new ErrorLogger();

    public ReservaReader(Validator validator) {
        this.validator = validator;
    }

    // ── Punto de entrada ──────────────────────────────────────────────────

    public void leer(String rutaArchivo) {
        if (AppConfig.FILE_RESERVAS.equals(rutaArchivo)) {
            leerSimple(rutaArchivo);
        } else if (AppConfig.FILE_MAESTRO.equals(rutaArchivo)
                || AppConfig.FILE_MAESTRO_ERRORES.equals(rutaArchivo)) {
            leerMaestro(rutaArchivo);
        } else {
            System.err.println("Archivo no reconocido: " + rutaArchivo);
        }
    }

    // ── Lectura simple (reservas.txt) ─────────────────────────────────────

    private void leerSimple(String rutaArchivo) {
        int totalReservas = 0;
        int totalBusiness = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                System.out.println("  " + linea);
                totalReservas++;
                if (linea.contains("Business")) totalBusiness++;
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + rutaArchivo + ": " + e.getMessage());
        }

        System.out.println("Número de reservas:          " + totalReservas);
        System.out.println("Número de reservas Business: " + totalBusiness);
    }

    // ── Lectura maestra (con validación y split por destino) ──────────────

    private void leerMaestro(String rutaArchivo) {
        boolean esConErrores = rutaArchivo.equals(AppConfig.FILE_MAESTRO_ERRORES);

        LinkedHashMap<String, List<String>> porDestino = new LinkedHashMap<>();
        leerYClasificar(rutaArchivo, porDestino);

        if (esConErrores) {
            System.out.println("\n=== Log de errores ===");
            errorLogger.mostrar();
        }

        guardarPorDestino(porDestino);
    }

    /**
     * Lee el archivo línea a línea y, por cada línea válida, la añade
     * al mapa agrupado por destino.
     */
    private void leerYClasificar(String rutaArchivo,
                                  LinkedHashMap<String, List<String>> porDestino) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    procesarLinea(linea, porDestino);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + rutaArchivo + ": " + e.getMessage());
        }
    }

    /**
     * Valida la línea; si es válida la agrupa por destino (máximo
     * {@link AppConfig#MAX_DESTINOS_RECIENTES} destinos), si no la loguea.
     */
    private void procesarLinea(String linea,
                                LinkedHashMap<String, List<String>> porDestino) {
        Validator.ValidationResult resultado = validator.validar(linea);
        if (!resultado.isValido()) {
            errorLogger.registrar(linea, resultado.getMensajeError());
            return;
        }

        String destino = linea.split(AppConfig.SEPARADOR)[3].trim();
        porDestino.computeIfAbsent(destino, k -> new ArrayList<>()).add(linea);

        // Conservar únicamente los últimos MAX_DESTINOS_RECIENTES destinos únicos
        if (porDestino.size() > AppConfig.MAX_DESTINOS_RECIENTES) {
            String masAntiguo = porDestino.keySet().iterator().next();
            porDestino.remove(masAntiguo);
        }
    }

    /**
     * Por cada destino: carga líneas existentes del archivo, añade las
     * nuevas que no sean duplicado y muestra el recuento total.
     */
    private void guardarPorDestino(LinkedHashMap<String, List<String>> porDestino) {
        System.out.println("\n=== Archivos por destino ===");

        if (porDestino.isEmpty()) {
            System.out.println("  (no se generaron archivos por destino)");
            return;
        }

        for (Map.Entry<String, List<String>> entrada : porDestino.entrySet()) {
            String       destino      = entrada.getKey();
            List<String> nuevasLineas = entrada.getValue();
            String       rutaDestino  = AppConfig.PREFIJO_DESTINO + destino + ".txt";

            Set<String> yaExistentes = cargarLineasExistentes(rutaDestino);
            int nuevasEscritas = 0;

            try (BufferedWriter bw = new BufferedWriter(
                    new FileWriter(rutaDestino, true))) {
                for (String linea : nuevasLineas) {
                    if (!yaExistentes.contains(linea)) {
                        bw.write(linea);
                        bw.newLine();
                        nuevasEscritas++;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error al escribir " + rutaDestino + ": " + e.getMessage());
                continue;
            }

            int total = yaExistentes.size() + nuevasEscritas;
            System.out.printf("  → %-45s (%d reservas en total)%n",
                rutaDestino, total);
        }
    }

    /**
     * Devuelve el conjunto de líneas que ya existen en un archivo.
     * Si el archivo no existe devuelve un conjunto vacío.
     */
    private Set<String> cargarLineasExistentes(String rutaArchivo) {
        Set<String> existentes = new HashSet<>();
        File f = new File(rutaArchivo);
        if (!f.exists()) return existentes;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                existentes.add(linea.trim());
            }
        } catch (IOException e) {
            System.err.println("Error leyendo existentes de " + rutaArchivo
                + ": " + e.getMessage());
        }
        return existentes;
    }
}
