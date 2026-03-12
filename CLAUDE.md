# OeKB Visual Client - Developer Guide

## Overview

OeKB Visual Client is a JavaFX 25 desktop application for interacting with OeKB's Financial Data Platform (FDP). This application provides a modern, professional interface for financial institutions to manage access rights, upload data, download reports, and perform regulatory reporting tasks.

## Technology Stack

- **Java 17** - Target and source compatibility
- **JavaFX 25** - UI framework (native components, no JFoenix)
- **Gradle 9.2.1** - Build system with Kotlin DSL
- **Ikonli 12.4.0** - Font icon library (Bootstrap, Win10, Feather, CoreUI, FontAwesome packs)
- **Apache POI 5.5.0** - Excel file processing
- **Apache HttpComponents 5.4.1** - HTTP client for API communication
- **Log4j 2.24.3** - Logging framework
- **JUnit 5.11.4 / Mockito 5.14.2** - Testing framework

## Quick Start

### Build the Application
```bash
./gradlew build
```

### Run the Application
```bash
./gradlew run
```

### Run Tests
```bash
./gradlew test
```

### Clean Build
```bash
./gradlew clean build
```

### Code Quality Checks
```bash
# Run all checks (tests + JaCoCo + PMD + Checkstyle + SpotBugs)
./gradlew check

# Format code with Spotless
./gradlew spotlessApply

# Check formatting without fixing
./gradlew spotlessCheck

# Individual tool reports
./gradlew jacocoTestReport    # Coverage → build/reports/jacoco/
./gradlew pmdMain             # PMD → build/reports/pmd/
./gradlew checkstyleMain      # Checkstyle → build/reports/checkstyle/
./gradlew spotbugsMain        # SpotBugs → build/reports/spotbugs/
```

Configuration files are in the `config/` directory.

### Create Distribution Packages
```bash
# Create runtime image with jlink (via org.beryx.runtime plugin)
./gradlew runtime

# Create Windows installer with jpackage (requires Windows + WiX Toolset)
./gradlew jpackage

# Create app image without installer
./gradlew jpackageImage

# Create portable ZIP
./gradlew createPortableZip

# Suggest which JDK modules are needed by dependencies
./gradlew suggestModules

# Build everything at once (installer + portable ZIP)
./gradlew buildDistribution
```

See [BUILD.md](BUILD.md) for detailed build instructions and GitHub Actions setup.

## Architecture

### Project Structure
```
src/main/
├── java/
│   ├── common/              # Utility classes (XMLHelper)
│   ├── controller/          # FXML controllers for each page (16 classes)
│   ├── dao/                 # Data access objects (HTTP, Access Rights, Excel)
│   ├── model/               # Data models and business objects
│   └── StartApp.java        # Application entry point
└── resources/
    ├── css/                 # Stylesheets (modern-theme.css, client.css, color-functions.css)
    ├── sass/                # SCSS source (client.scss → client.css)
    ├── fonts/               # Custom fonts (OpenSans.ttf)
    ├── pages/               # FXML UI definitions (16 pages)
    ├── img/                 # Image resources (minimal, using font icons)
    ├── xsd/                 # XML Schema definitions (FundsXML, OeNBCheck)
    └── log4j2.properties    # Logging configuration
src/test/
├── java/
│   ├── common/              # XMLHelperTest
│   └── dao/                 # FundEnhancerTest, OeKBHTTPTest
└── resources/
    └── mockito-extensions/  # Mockito configuration
```

### Design Patterns

#### 1. Dependency Injection via Interface
`IApplicationSettings` is an interface that abstracts application settings, enabling dependency injection for testability. Controllers and DAOs depend on `IApplicationSettings` rather than the concrete `ApplicationSettings` class.

#### 2. FXML-Controller Pattern
Each page has an FXML file paired with a Java controller:
- `pageMain.fxml` ↔ `MainController.java`
- `pageAbout.fxml` ↔ `About.java`
- `pageApplicationSettings.fxml` ↔ `ApplicationSettings.java`
- `pageAccessRightGrant.fxml` ↔ `AccessRightGrant.java`
- `pageAccesRightsReceived.fxml` ↔ `AccessRightsReceived.java`
- `pageAvailableData.fxml` ↔ `AvailableData.java`
- `pageDataUpload.fxml` ↔ `DataUpload.java`
- `pageDocumentDownload.fxml` ↔ `DocumentDownload.java`
- `pageFundDownload.fxml` ↔ `FundDownload.java`
- `pageJournal.fxml` ↔ `JournalController.java`
- `pageNewInformation.fxml` ↔ `NewInformationController.java`
- `pageOFI_new.fxml` ↔ `OFI.java`
- `pageOwnDataDownloaded.fxml` ↔ `OwnDataDownloaded.java`
- `pageRegulatoryReporting.fxml` ↔ `RegulatoryReportingDownload.java`
- `pageShareClassDownload.fxml` ↔ `ShareClassDownload.java`
- `dialogAccessRuleEdit.fxml` ↔ `AccessRuleEditDialog.java`

Controllers handle UI logic and user interactions, while FXML defines the layout.

#### 3. Professional UI Theme
The application uses a conservative financial institution design:
- **Primary Color**: `#003d5c` (Deep Blue - Trust & Stability)
- **Accent Color**: `#c8102e` (Austrian Red - OeKB branding)
- **Design Philosophy**: Conservative, professional, minimal animations
- **Border Radius**: 3-4px (traditional look)
- **CSS Classes**: Use `-compact` suffix for space-efficient layouts

#### 4. Icon Integration
All menu icons use Ikonli font icons (Bootstrap, Win10, Feather, CoreUI, FontAwesome packs):
```xml
<?import org.kordamp.ikonli.javafx.FontIcon?>

<Button text="Settings">
    <graphic>
        <FontIcon iconLiteral="bi-gear-fill" iconSize="20" iconColor="WHITE"/>
    </graphic>
</Button>
```

**IMPORTANT**: Wenn du Icons von Ikonli hinzufügst, schaue zuerst in den jeweiligen Cheat Sheet nach, ob dieses Icon auch existiert. Die Cheat Sheets sind unter https://kordamp.org/ikonli/ verlinkt.

Common mistakes:
- ❌ `bi-clipboard-check-fill` → ✅ `bi-clipboard-check`
- ❌ `bi-database-fill` → ✅ `bi-hdd-stack-fill`
- ❌ `bi-bar-chart-fill` → ✅ `bi-bar-chart-line-fill`

## Key Development Guidelines

### Adding New Icons
1. Visit https://kordamp.org/ikonli/cheat-sheet-bootstrapicons.html
2. Verify the exact icon literal exists
3. Use format: `<FontIcon iconLiteral="bi-icon-name" iconSize="20" iconColor="WHITE"/>`

### Creating Scrollable Forms
For forms that need to work on smaller screens:
```xml
<ScrollPane fitToWidth="true" hbarPolicy="NEVER" vbarPolicy="AS_NEEDED" pannable="true">
    <content>
        <VBox styleClass="mainPane" spacing="12">
            <!-- Use -compact CSS classes -->
            <Label text="Title" styleClass="page-title-compact"/>
            <VBox styleClass="card-compact" spacing="10">
                <!-- Form elements -->
            </VBox>
        </VBox>
    </content>
</ScrollPane>
```

### CSS Styling Best Practices
- Use existing CSS classes from `modern-theme.css`
- For compact layouts, use `-compact` suffix classes
- Maintain professional color palette (no bright gradients)
- Keep border-radius values conservative (3-4px)
- Test on smaller screens (forms should scroll, not overflow)

### Adding New Menu Items
1. Edit `src/main/resources/pages/pageMain.fxml`
2. Add button with FontIcon graphic (verify icon exists first!)
3. Create corresponding controller method in `MainController.java`
4. Create FXML page and controller class
5. Update CSS if needed for styling

## Common Tasks

### Update Application Version
Edit `build.gradle.kts`:
```kotlin
version = "0.4"  // Update this line
```

### Add New Dependencies
Edit `build.gradle.kts` dependencies block:
```kotlin
dependencies {
    implementation("group:artifact:version")
}
```
Then run `./gradlew build --refresh-dependencies`

### Modify UI Theme
Edit `src/main/resources/css/modern-theme.css`:
- Color variables are defined in `.root` selector
- Use CSS variables for consistency: `-primary-color`, `-accent-color`, etc.
- Test changes by running the application

### Debug Logging
Application logs are written to console and `logs/` directory.
Configure in `src/main/resources/log4j2.properties`.

## Migration Notes

This application was recently migrated from:
- **Groovy → Java** (all controllers and models)
- **JFoenix 9.0.10 → Native JavaFX 25** (removed Material Design library)
- **PNG Icons → Ikonli Font Icons** (reduced image resources by 200KB+)
- **Groovy DSL → Kotlin DSL** (build.gradle → build.gradle.kts)

If you find legacy JFoenix references or Groovy code, they should be removed/converted.

## Known Issues

### CSS Warnings
Non-critical warnings about shadow variables:
```
CSS Error parsing: Unexpected TOKEN at [line,col]
```
These don't affect functionality and relate to drop-shadow syntax variations.

### Git Authentication
Pushing to GitHub requires authentication. Use SSH keys or configure Git credentials:
```bash
git config credential.helper store
git push
```

## Application Features

### Core Modules
1. **About** - Application information and version details
2. **Settings** - Configure OeKB credentials, proxy settings, application options
3. **Access Rights Received** - View received data access permissions
4. **Grant Rights** - Manage access rights for other institutions
5. **Data Upload** - Upload financial data to OeKB FDP
6. **Fund Download** - Download fund data
7. **ShareClass Download** - Download share class information
8. **Documents** - Document management
9. **Regulatory Reporting** - Compliance reporting tools
10. **Available Data** - Browse available datasets
11. **Own Data Downloaded** - View own downloaded data history
12. **Journal** - Activity journal and audit log
13. **New Information** - View new information notifications
14. **OFI** - OFI (Offene Fonds Information) management

### Data Supplier (DDS)
The application supports switching between different Data Supplier IDs via the menu sidebar.

### Server Toggle
Production vs. Test server toggle available in the menu sidebar.

## Resources

- **Ikonli Documentation**: https://kordamp.org/ikonli/
- **Bootstrap Icons Cheat Sheet**: https://kordamp.org/ikonli/cheat-sheet-bootstrapicons.html
- **JavaFX 25 Documentation**: https://openjfx.io/javadoc/25/
- **Gradle Kotlin DSL**: https://docs.gradle.org/current/userguide/kotlin_dsl.html

## Development Tips

1. **Always verify icon codes** before adding them to FXML files
2. **Use compact CSS classes** for forms that need to fit smaller screens
3. **Test scrolling behavior** on settings and form pages
4. **Maintain professional aesthetic** - avoid bright colors and excessive animations
5. **Check logs** after UI changes to catch CSS or FXML errors early
6. **Run `./gradlew build`** before committing to catch compilation issues

## Contact

For questions about the OeKB FDP API or business requirements, refer to:
- `FDP-Java-Client_V2-4_202202-1.pdf` (API documentation)
- OeKB support channels (institution-specific)
