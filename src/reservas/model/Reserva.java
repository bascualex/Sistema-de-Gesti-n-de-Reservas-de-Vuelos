package reservas.model;

import reservas.config.AppConfig;

/**
 * Representa una reserva de vuelo.
 * Inmutable tras su construcción.
 */
public class Reserva {

    private final String asiento;
    private final String nombre;
    private final String clase;
    private final String destino; // null en el formato simple (reservas.txt)

    public Reserva(String asiento, String nombre, String clase, String destino) {
        this.asiento  = asiento;
        this.nombre   = nombre;
        this.clase    = clase;
        this.destino  = destino;
    }

    // ── Factory ────────────────────────────────────────────────────────────

    /**
     * Crea una {@link Reserva} a partir de una línea de texto.
     * Acepta 3 campos (sin destino) o 4 campos (con destino).
     *
     * @return instancia de Reserva, o {@code null} si la línea no tiene
     *         3 ni 4 campos.
     */
    public static Reserva fromLinea(String linea) {
        if (linea == null || linea.isBlank()) return null;
        String[] campos = linea.split(AppConfig.SEPARADOR);
        if (campos.length == 3) {
            return new Reserva(campos[0].trim(), campos[1].trim(),
                               campos[2].trim(), null);
        }
        if (campos.length == 4) {
            return new Reserva(campos[0].trim(), campos[1].trim(),
                               campos[2].trim(), campos[3].trim());
        }
        return null;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    public String getAsiento()  { return asiento; }
    public String getNombre()   { return nombre; }
    public String getClase()    { return clase; }
    public String getDestino()  { return destino; }

    public boolean esBusiness()    { return "Business".equals(clase); }
    public boolean tieneDestino()  { return destino != null; }

    // ── Representación textual (mismo formato que los archivos) ───────────

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append(asiento).append(AppConfig.SEPARADOR)
            .append(nombre).append(AppConfig.SEPARADOR)
            .append(clase);
        if (destino != null) {
            sb.append(AppConfig.SEPARADOR).append(destino);
        }
        return sb.toString();
    }
}
