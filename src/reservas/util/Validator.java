package reservas.util;

import reservas.config.AppConfig;

import java.util.List;

/**
 * Valida una línea de reserva maestro contra los valores de referencia.
 */
public class Validator {

    private final List<String> asientosValidos;
    private final List<String> nombresValidos;
    private final List<String> clasesValidas;
    private final List<String> destinosValidos;

    public Validator(List<String> asientosValidos,
                     List<String> nombresValidos,
                     List<String> clasesValidas,
                     List<String> destinosValidos) {
        this.asientosValidos  = asientosValidos;
        this.nombresValidos   = nombresValidos;
        this.clasesValidas    = clasesValidas;
        this.destinosValidos  = destinosValidos;
    }

    /**
     * Valida la línea y devuelve un {@link ValidationResult} con el
     * resultado y, en caso de error, el mensaje descriptivo.
     */
    public ValidationResult validar(String linea) {
        String[] campos = linea.split(AppConfig.SEPARADOR);

        if (campos.length != 4) {
            return ValidationResult.error(
                "Sobra o falta un campo o más. El formato debe ser " +
                "[Asiento], [Nombre], [Clase], [Destino]");
        }

        String asiento = campos[0].trim();
        String nombre  = campos[1].trim();
        String clase   = campos[2].trim();
        String destino = campos[3].trim();

        if (!asientosValidos.contains(asiento)) {
            return ValidationResult.error(
                "El campo [Asiento] '" + asiento + "' no es válido. " +
                "El formato debe ser [Asiento], [Nombre], [Clase], [Destino]");
        }
        if (!nombresValidos.contains(nombre)) {
            return ValidationResult.error(
                "El campo [Nombre] '" + nombre + "' no es válido. " +
                "El formato debe ser [Asiento], [Nombre], [Clase], [Destino]");
        }
        if (!clasesValidas.contains(clase)) {
            return ValidationResult.error(
                "El campo [Clase] '" + clase + "' no es válido. " +
                "El formato debe ser [Asiento], [Nombre], [Clase], [Destino]");
        }
        if (!destinosValidos.contains(destino)) {
            return ValidationResult.error(
                "El campo [Destino] '" + destino + "' no es válido. " +
                "El formato debe ser [Asiento], [Nombre], [Clase], [Destino]");
        }

        return ValidationResult.ok();
    }

    // ── Resultado de validación ───────────────────────────────────────────

    public static final class ValidationResult {
        private final boolean valido;
        private final String  mensajeError;

        private ValidationResult(boolean valido, String mensajeError) {
            this.valido        = valido;
            this.mensajeError  = mensajeError;
        }

        public static ValidationResult ok()             { return new ValidationResult(true, null); }
        public static ValidationResult error(String msg){ return new ValidationResult(false, msg); }

        public boolean isValido()        { return valido; }
        public String getMensajeError()  { return mensajeError; }
    }
}
