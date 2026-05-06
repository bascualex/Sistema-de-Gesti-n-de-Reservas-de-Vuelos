package reservas.util;

import reservas.config.AppConfig;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Escribe y muestra entradas en el log de errores ({@code registro_errores.log}).
 */
public class ErrorLogger {

    private final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(AppConfig.FORMATO_FECHA);

    /**
     * Añade una entrada de error al log.
     *
     * @param lineaOriginal  línea del archivo que provocó el error
     * @param mensajeError   descripción del problema
     */
    public void registrar(String lineaOriginal, String mensajeError) {
        String timestamp = LocalDateTime.now().format(formatter);
        String entrada   = timestamp + AppConfig.SEPARADOR
                         + lineaOriginal + AppConfig.SEPARADOR
                         + mensajeError;

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(AppConfig.FILE_LOG_ERRORES, true))) {
            bw.write(entrada);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }

    /**
     * Imprime por consola el contenido actual del log.
     * Si el log está vacío indica que no hubo errores.
     */
    public void mostrar() {
        File logFile = new File(AppConfig.FILE_LOG_ERRORES);
        if (!logFile.exists() || logFile.length() == 0) {
            System.out.println("  (sin errores registrados)");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("  " + linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el log de errores: " + e.getMessage());
        }
    }
}
