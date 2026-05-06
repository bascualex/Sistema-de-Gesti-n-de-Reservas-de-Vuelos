package reservas.config;

/**
 * Constantes globales de la aplicacion.
 * Centraliza rutas de archivos, separadores y limites numericos.
 */
public final class AppConfig {

    private AppConfig() {}

    private static final String SEP = java.io.File.separator;

    // -- Directorio de datos --------------------------------------------------
    public static final String DIRECTORIO_DATOS = "data" + SEP;

    // -- Archivos -------------------------------------------------------------
    public static final String FILE_RESERVAS        = DIRECTORIO_DATOS + "reservas.txt";
    public static final String FILE_MAESTRO         = DIRECTORIO_DATOS + "reservas_maestro.txt";
    public static final String FILE_MAESTRO_ERRORES = DIRECTORIO_DATOS + "reservas_maestro_con_errores.txt";
    public static final String FILE_LOG_ERRORES     = DIRECTORIO_DATOS + "registro_errores.log";
    public static final String PREFIJO_DESTINO      = DIRECTORIO_DATOS + "reservas_";

    // -- Parametros de negocio ------------------------------------------------
    public static final int RESERVAS_POR_LOTE      = 4;
    public static final int MAX_DESTINOS_RECIENTES = 4;

    // -- Formato --------------------------------------------------------------
    public static final String SEPARADOR     = ", ";
    public static final String FORMATO_FECHA = "dd/MM/yyyy HH:mm:ss";
}
