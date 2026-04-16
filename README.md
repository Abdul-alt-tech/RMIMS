# RMIMS

Raw Materials Inventory Management System (RMIMS) is a Java Swing desktop application built to manage school inventory, procurement, stores, and transaction records. It uses Microsoft SQL Server for data storage and demonstrates a layered architecture with separate UI, data access, and utility components.

## What this project is

- Java Swing application for inventory and procurement management.
- Stores data in Microsoft SQL Server.
- Includes login authentication, inventory tracking, procurement shopping lists, store management, and reporting.
- Designed as an academic project with clear separation between UI, DAOs, models, and utilities.

## Key features

- Secure login screen with user authentication.
- Inventory list with low-stock detection.
- Procurement/shopping list creation for items that need reorder.
- Store management and supplier tracking.
- Transaction logging for stock withdrawals and receipts.
- Reports panel with monthly summary and email-ready report generation.

## Prerequisites

- Java JDK 11 or newer
- Microsoft SQL Server accessible to the app
- `lib/mssql-jdbc-13.2.1.jre11.jar` included for JDBC
- `ddl/mssql-jdbc_auth-13.2.1.x64.dll` included for Windows authentication support

## Build & run

### Compile

From the project root:

```bash
javac -d bin -cp "lib/mssql-jdbc-13.2.1.jre11.jar" src\\com\\nalex\\rmims\\**\\*.java
```

### Run

```bash
java -cp "bin;lib/mssql-jdbc-13.2.1.jre11.jar" -Djava.library.path=ddl com.nalex.rmims.main.RMIMSApp
```

> If you use an IDE such as IntelliJ IDEA or Eclipse, import the project as a Java project and add `lib` to the classpath. Also configure `ddl` as the native library path.

## Database configuration

The database connection is managed in:

- `src/com/nalex/rmims/util/DatabaseConnection.java`

Update the connection URL, database name, username, and password there to match your SQL Server environment.

## Project structure

- `src/` — Java source code
  - `com/nalex/rmims/main/RMIMSApp.java` — application entry point
  - `com/nalex/rmims/gui/` — Swing UI frames, panels, and dialogs
  - `com/nalex/rmims/dao/` — data access objects for database operations
  - `com/nalex/rmims/model/` — domain model classes
  - `com/nalex/rmims/util/` — utilities for DB connection, hashing, email, and testing
- `bin/` — compiled `.class` files
- `lib/` — third-party libraries and driver jars
- `ddl/` — native DLLs or database scripts
- `Projectdoc.md` — developer guide and project documentation

## Helpful commands

- Test database connectivity:

```bash
java -cp "bin;lib/mssql-jdbc-13.2.1.jre11.jar" com.nalex.rmims.util.TestConnection
```

## Notes for collaborators

- The repository currently includes generated `bin/` files and library jars. It may be helpful to add a `.gitignore` to exclude build artifacts.
- `Projectdoc.md` is the main project guide for developers; this README is a high-level introduction for GitHub visitors.

## Development tips

- Keep SQL logic inside DAO classes and UI logic inside `gui` classes.
- Use models in `src/com/nalex/rmims/model` to pass data between the UI and DAOs.
- Prefer small, focused methods and clear exception handling for database operations.
