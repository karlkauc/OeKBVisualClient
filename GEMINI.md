# Project Overview

This project is a JavaFX desktop application called "OeKB Visual Client". It appears to be a client for the OeKB (Oesterreichische Kontrollbank) Financial Data Platform. The application allows users to interact with the platform, likely for downloading and uploading financial data.

The application is built with Java 17 and Gradle. It uses the following key technologies:

*   **User Interface:** JavaFX is used for the user interface. The UI is defined in FXML files located in `src/main/resources/pages`.
*   **HTTP Client:** Apache HttpClient is used for making HTTP requests to the OeKB API.
*   **Excel Support:** Apache POI is used for working with Excel files, suggesting that data can be exported to or imported from Excel.
*   **Logging:** Log4j 2 and SLF4J are used for logging.

The application follows a Model-View-Controller (MVC) architecture, with the code organized into `model`, `view` (FXML files), and `controller` packages.

# Building and Running

The project is built using Gradle. The following are the key Gradle tasks:

*   `./gradlew build`: Compiles the source code, runs tests, and builds the JAR file.
*   `./gradlew run`: Runs the application.
*   `./gradlew jlink`: Creates a custom runtime image using jlink.
*   `./gradlew jpackage`: Creates a Windows MSI installer using jpackage.
*   `./gradlew createPortableZip`: Creates a portable ZIP package of the application.
*   `./gradlew buildDistribution`: Builds the complete distribution with a runtime image, installer, and portable ZIP.

To run the application, you can use the following command:

```bash
./gradlew run
```

# Development Conventions

*   **Code Style:** The code follows standard Java conventions.
*   **Testing:** JUnit 5 is used for testing. Tests are located in the `src/test` directory.
*   **Dependencies:** Dependencies are managed using Gradle in the `build.gradle.kts` file.
*   **UI:** The user interface is defined in FXML files in `src/main/resources/pages`. The corresponding controllers are in `src/main/java/controller`.
*   **Logging:** Use the SLF4J API for logging. The logging configuration is in `src/main/resources/log4j2.properties`.
