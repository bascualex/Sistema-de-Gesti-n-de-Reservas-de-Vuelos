package reservas;

import reservas.config.AppConfig;
import reservas.service.ReservaCreator;
import reservas.service.ReservaReader;
import reservas.util.Validator;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Gestión de Reservas de Vuelos.
 */
public class Main {

    public static void main(String[] args) {
        inicializarDirectorioDatos();

        ReservaCreator creator   = new ReservaCreator();
        Validator      validator = new Validator(
            creator.getAsientosValidos(),
            creator.getNombres(),
            creator.getClases(),
            creator.getDestinos()
        );
        ReservaReader reader = new ReservaReader(validator);

        boolean ejecucion = true;
        try (Scanner sc = new Scanner(System.in)) {
            while (ejecucion) {
                mostrarMenu();
                String opcion = sc.nextLine().trim();

                switch (opcion) {
                    case "1":
                        System.out.println("\n── Parte 1: Reservas simples ──────────────────");
                        creator.crear(AppConfig.FILE_RESERVAS, false);
                        reader.leer(AppConfig.FILE_RESERVAS);
                        break;

                    case "2":
                        System.out.println("\n── Parte 2: Reservas maestro ──────────────────");
                        creator.crear(AppConfig.FILE_MAESTRO, true);
                        reader.leer(AppConfig.FILE_MAESTRO);
                        break;

                    case "3":
                        System.out.println("\n── Parte 3: Reservas con errores ──────────────");
                        creator.crearConErrores();
                        reader.leer(AppConfig.FILE_MAESTRO_ERRORES);
                        break;

                    case "4":
                        System.out.println("Saliendo...");
                        ejecucion = false;
                        break;

                    default:
                        System.out.println("Opción no válida. Introduce 1, 2, 3 o 4.");
                }
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Sistema de Reservas de Vuelos      ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  1) Parte 1 – Reservas simples       ║");
        System.out.println("║  2) Parte 2 – Reservas maestro       ║");
        System.out.println("║  3) Parte 3 – Reservas con errores   ║");
        System.out.println("║  4) Salir                            ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("¿Qué deseas hacer? ");
    }

    /**
     * Resuelve la raiz del proyecto y crea data/ dentro de ella.
     * IntelliJ sin Maven/Gradle compila a out/production/<modulo>/,
     * subiendo 3 niveles llegamos a la raiz del proyecto.
     */
    private static void inicializarDirectorioDatos() {
        try {
            File classesDir = new File(
                Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            // out/production/<modulo>  ->  3 niveles arriba = raiz
            File projectRoot = classesDir.getParentFile()  // production
                                         .getParentFile()  // out
                                         .getParentFile(); // raiz del proyecto

            if (projectRoot != null && projectRoot.exists()) {
                System.setProperty("user.dir", projectRoot.getAbsolutePath());
            }
        } catch (URISyntaxException | NullPointerException e) {
            // Usar working directory actual si no se puede resolver
        }

        File dataDir = new File(System.getProperty("user.dir"), AppConfig.DIRECTORIO_DATOS);
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            System.err.println("No se pudo crear: " + dataDir.getAbsolutePath());
        }
        System.out.println("Archivos guardados en: " + dataDir.getAbsolutePath());
    }
}
