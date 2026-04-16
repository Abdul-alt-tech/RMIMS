# RMIMS — Junior Developer Guide

Short, practical guide for new developers joining the RMIMS project.

**What this project is**
- RMIMS is a simple Java Swing-based Retail/Materials Inventory Management System that uses MS SQL Server for storage.

**Quick goals for you**
- Get the project building and running locally.  
- Understand the core layers: UI (Swing), DAOs, Models, and DB utilities.  
- Learn how to change the DB connection and run updates safely.

## Prerequisites
- Java JDK 11 or newer installed and `JAVA_HOME` set.  
- Microsoft SQL Server accessible for the app to connect.  
- The JDBC driver jar is stored in `lib/mssql-jdbc-13.2.1.jre11.jar`.  
- The native auth DLL is in `ddl/mssql-jdbc_auth-13.2.1.x64.dll` (Windows).

## Build & run (Windows - simple CLI)
1. Compile sources into `bin` (from project root):
```bash
javac -d bin -cp "lib/mssql-jdbc-13.2.1.jre11.jar" src\\com\\nalex\\rmims\\**\\*.java
```
2. Run the application (set library path for native auth DLL):
```bash
java -cp "bin;lib/mssql-jdbc-13.2.1.jre11.jar" -Djava.library.path=ddl com.nalex.rmims.main.RMIMSApp
```
Notes: IDEs like IntelliJ or Eclipse can import this as a Java project — add `lib` to the classpath and `ddl` to native library path.

## Where to change DB settings
- Edit `src/com/nalex/rmims/util/DatabaseConnection.java` to set server, database, username, and password.  
- Keep credentials out of commits — prefer a local configuration or environment variables for production use.

## Project layout (high-level)
- `bin/` — compiled `.class` files (build output).  
- `lib/` — third-party jars (MSSQL JDBC driver).  
- `ddl/` — native DLLs or DB scripts (contains JDBC auth DLL).  
- `src/com/nalex/rmims/main/RMIMSApp.java` — application entry point (initializes UI).  
- `src/com/nalex/rmims/util/DatabaseConnection.java` — manages JDBC connections.  
- `src/com/nalex/rmims/util/PasswordHasher.java` — password hashing utilities.  
- `src/com/nalex/rmims/model/` — data classes: `User`, `Item`, `Category`, `Store`, `Transaction`, `Procurement`, `ProcurementItem`.  
- `src/com/nalex/rmims/dao/` — DAOs for CRUD operations (e.g., `ItemDAO`, `UserDAO`, `TransactionDAO`).  
- `src/com/nalex/rmims/gui/` — Swing UI: frames, panels, and dialogs (login, main menu, inventory, procurements, reports).  

## Typical tasks for a junior dev
- Add a new field to a model: update the model class, the DAO SQL, and any UI forms that display/edit it.  
- Add a simple UI action: modify the appropriate panel (`gui/`) and call the DAO.  
- Fix a bug: reproduce, add logging, write a small focused change, and test manually.

## Coding tips & conventions
- Keep methods short and single-purpose.  
- DAOs handle only SQL and mapping between `ResultSet` and model objects.  
- UI code (Swing) belongs in `gui` classes; keep business logic in DAOs or new service classes.  
- Handle SQLExceptions close to the DAO and return meaningful status or throw a custom unchecked exception.

## Testing & debugging
- To test DB connectivity, run `TestConnection` (`src/com/nalex/rmims/util/TestConnection.java`).  
- Common problems:
  - `ClassNotFoundException`: ensure `lib/mssql-jdbc-13.2.1.jre11.jar` is on classpath.  
  - Authentication errors: check credentials and SQL Server configuration (enable TCP/IP).  
  - Native auth errors: set `-Djava.library.path=ddl` when running on Windows.

## Making safe DB changes
- Always back up schema/data before running DDL or migrations.  
- Prefer writing reversible SQL scripts and keep them in a `database/` or `ddl/` folder.  

  
