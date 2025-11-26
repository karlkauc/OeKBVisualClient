# OeKB Visual Client

A desktop client for the OeKB (Oesterreichische Kontrollbank) Financial Data Platform (FDP). This application provides a graphical user interface for interacting with the OeKB FDP services, designed for system users ("Systemuser").

## Core Features

*   **Access Rights Management:** Download and upload access rule definitions.
*   **Data Download:**
    *   Download various data types including Funds, Share Classes, Documents, and Regulatory Reportings.
    *   Check for newly available data.
    *   Monitor statistics on who has downloaded your own data.
*   **OeNB Reporting:** Specialized interface for downloading OeNB-specific reports like "Aggregierung", "SecBySec", and "Check".
*   **Data Upload:** Upload data files directly to the FDP.
*   **Activity Journal:** View a log of all past upload and download activities.

## Technologies Used

*   **Language:** Java 17
*   **Framework:** JavaFX for the user interface.
*   **Build Tool:** Gradle
*   **Key Libraries:**
    *   Apache HttpClient for API communication.
    *   Apache POI for Excel import/export functionality.
    *   Log4j 2 for logging.

## Setup and Running

This project is built using the Gradle wrapper.

### Prerequisites

*   JDK 17 or higher.

### Building

To build the application and create a JAR file, run:
```bash
./gradlew build
```

### Running the Application

To run the application directly from the source, use:
```bash
./gradlew run
```

### Creating a Distribution

To create a native application bundle (including a Windows installer), run:
```bash
./gradlew buildDistribution
```
The output will be located in `build/jpackage`.

## Architecture

The application follows a standard Model-View-Controller (MVC) pattern:
*   **Model:** Data objects located in `src/main/java/model`.
*   **View:** UI layouts defined as FXML files in `src/main/resources/pages`.
*   **Controller:** UI logic and event handling located in `src/main/java/controller`.
*   **DAO (Data Access Object):** Classes for interacting with the OeKB API and local files are in `src/main/java/dao`.

# Download

[Download the latest release here](https://github.com/karlkauc/OeKBVisualClient/releases)

Nur Systemuser - kein WebUser

# More information

Visit the [project page](http://karlkauc.github.io/OeKBVisualClient) for more information.

# Screenshots

![Screenshot01](docs/screenshot-01.png)

# License

Apache License, Version 2.0

By using this software you agree to

Oracle Binary Code License Agreement for the Java SE Platform Products and JavaFX
Oracle Technology Network Early Adopter Development License Agreement in case of EA releases
Use it at your own risk.
