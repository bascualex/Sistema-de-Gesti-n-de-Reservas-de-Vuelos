# ✈️ Flight Booking Management System

> **Academic project** — built as a coursework assignment to practice Java fundamentals: file I/O, modular design, data validation, and error handling.

---

## Overview

A command-line Java application that simulates a flight reservation management system. The program generates random bookings, validates them against a master registry, splits valid reservations into per-destination files, and logs any malformed entries with timestamps.

The original single-file implementation was refactored into a layered, modular architecture following clean code principles.

---

## Features

- Random booking generation with seat assignment (no duplicates per session)
- Three operating modes: simple bookings, master registry, and error-testing
- Field-level validation with descriptive error messages
- Timestamped error logging to a persistent `.log` file
- Per-destination file output with duplicate prevention
- Cross-platform file path handling (`File.separator`)

---

## Architecture

The project follows a layered package structure:

```
src/reservas/
├── Main.java                    # Entry point & menu
├── config/
│   └── AppConfig.java           # Centralised constants (paths, limits, format)
├── model/
│   └── Reserva.java             # Immutable booking model with factory method
├── service/
│   ├── ReservaCreator.java      # Booking generation logic
│   └── ReservaReader.java       # File reading, validation & routing
└── util/
    ├── Validator.java            # Field-level validation, returns ValidationResult
    └── ErrorLogger.java          # Timestamped error log writer/reader
```

**Design decisions:**
- `Validator` is injected into `ReservaReader` (dependency injection) — easy to test or swap independently
- `ReservaCreator` separates `asientosDisponibles` (operational, shrinks per use) from `asientosValidos` (reference list for validation)
- All I/O uses try-with-resources to prevent resource leaks
- `AppConfig` is a non-instantiable utility class — all constants in one place

---

## Getting Started

**Requirements:** Java 11 or higher (Java 21 recommended)

**Compile:**
```bash
find src -name "*.java" | xargs javac -encoding UTF-8 -d out
```

**Run:**
```bash
java -cp out reservas.Main
```

**In IntelliJ IDEA:** Open the project, right-click `src/reservas/Main.java` → *Run 'Main.main()'*. The `data/` directory is created automatically on first run.

---

## Usage

The program presents an interactive menu with three modes:

| Option | Description | Output |
|--------|-------------|--------|
| 1 | Simple bookings | `data/reservas.txt` |
| 2 | Master registry with destinations | `data/reservas_maestro.txt` + `data/reservas_<Destination>.txt` |
| 3 | Error testing | `data/reservas_maestro_con_errores.txt` + `data/registro_errores.log` |

**Generated files** (all under `data/`):

- `reservas.txt` — plain bookings (seat, name, class)
- `reservas_maestro.txt` — full bookings (seat, name, class, destination)
- `reservas_maestro_con_errores.txt` — intentionally malformed entries for testing
- `reservas_<Destination>.txt` — one file per destination, no duplicates
- `registro_errores.log` — error log with timestamps and descriptions

---

## What I Learned

This project was my first time applying clean architecture principles to a Java codebase. The main refactoring challenges were:

- **Separating responsibilities** — the original code had reading, writing, and validation mixed in two classes. Breaking it into `model / service / util / config` layers made each piece much easier to reason about.
- **Resource management** — migrating raw `FileWriter`/`FileReader` calls to try-with-resources to prevent file handle leaks.
- **Dependency injection** — passing `Validator` into `ReservaReader` rather than instantiating it internally, which decouples validation rules from the reader.
- **Defensive validation** — returning a `ValidationResult` object instead of printing errors directly, keeping I/O separate from business logic.

---

## Author

Academic project — Java coursework.
