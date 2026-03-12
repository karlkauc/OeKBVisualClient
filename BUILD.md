# Build Instructions

## Native Windows Installer

This project uses the [badass-runtime plugin](https://github.com/beryx/badass-runtime-plugin) (`org.beryx.runtime` v2.0.1) to create native Windows installers with a bundled Java runtime via jlink and jpackage.

### Prerequisites

- **JDK 17 or higher** with jpackage support (included in OpenJDK 17+)
- **WiX Toolset** (for MSI installer on Windows): https://wixtoolset.org/
- **Gradle 9.2.1+** (included via wrapper)

### Building on Windows

#### 1. Build the Application
```bash
./gradlew build
```

#### 2. Create Runtime Image (jlink)
```bash
./gradlew runtime
```
This creates a custom JRE in `build/image/` with only required modules.

#### 3. Create Windows Installer (jpackage)
```bash
./gradlew jpackage
```
This creates a Windows MSI installer in `build/jpackage/`.

#### 4. Create Portable ZIP (No Installation Required)
```bash
./gradlew createPortableZip
```
This creates a portable ZIP file in `build/jpackage/` that can be extracted and run without installation.

#### 5. Build Everything (One Command)
```bash
./gradlew buildDistribution
```
This creates MSI installer, app-image, and portable ZIP.

### Installer Features

The generated Windows installer:
- **No Admin Rights Required** - Uses per-user installation
- **Custom Install Directory** - User can choose installation location
- **Start Menu Integration** - Adds application to Windows Start Menu
- **Desktop Shortcut** - Optional desktop shortcut
- **Bundled JRE** - No separate Java installation needed, stripped to minimum modules
- **Performance Optimized** - G1GC, String deduplication, tuned heap settings

### Available Gradle Tasks

| Task | Description |
|------|-------------|
| `./gradlew runtime` | Creates custom runtime image with jlink |
| `./gradlew runtimeZip` | Creates a ZIP of the runtime image |
| `./gradlew jpackage` | Creates Windows MSI installer |
| `./gradlew jpackageImage` | Creates app image without installer |
| `./gradlew suggestModules` | Suggests JDK modules needed by dependencies |
| `./gradlew createRuntimeImage` | Alias for `runtime` |
| `./gradlew createWindowsInstaller` | Alias for `jpackage` |
| `./gradlew createAppImage` | Alias for `jpackageImage` |
| `./gradlew createPortableZip` | Creates portable ZIP from app image |
| `./gradlew buildDistribution` | Builds everything (installer + portable ZIP) |

### Customization

Edit `build.gradle.kts` `runtime { }` block to customize:
- **App Version**: Change `version = "0.4"`
- **Vendor/Copyright**: In `jpackage { imageOptions / installerOptions }`
- **Installer Type**: Change `installerType = "msi"` to `"exe"`
- **JVM Flags**: In `jpackage { jvmArgs }`
- **JDK Modules**: In `runtime { modules.set(...) }`
- **Icon**: Place `.ico` file at `src/main/resources/img/connectdevelop.ico`

### Output Files

After successful build:
- **Runtime Image**: `build/image/`
- **MSI Installer**: `build/jpackage/OeKBVisualClient-0.4.msi`
- **Portable ZIP**: `build/jpackage/OeKBVisualClient-0.4-windows-x64.zip`
- **App Image** (unzipped): `build/jpackage/OeKBVisualClient/`

### Troubleshooting

#### "jpackage not found"
- Ensure you're using JDK 17+ (not JRE)
- Verify: `java -version` and `where jpackage`

#### "WiX Toolset not found"
- Install WiX Toolset: https://wixtoolset.org/
- Add WiX to PATH: `C:\Program Files (x86)\WiX Toolset v3.11\bin`

#### Icon not showing
- Convert PNG to ICO format (256x256 recommended)
- Place at: `src/main/resources/img/connectdevelop.ico`

#### Module errors
- Run `./gradlew suggestModules` to check which modules your dependencies need
- Update the `modules.set(...)` list in `build.gradle.kts` accordingly

## GitHub Actions - Automatic Builds

The repository includes a GitHub Actions workflow that automatically builds Windows installers when you create a new release.

### How to Create a Release

1. **Tag the release**:
   ```bash
   git tag v0.4
   git push origin v0.4
   ```

2. **Create GitHub Release**:
   - Go to: https://github.com/karlkauc/OeKBVisualClient/releases/new
   - Choose tag: `v0.4`
   - Write release notes
   - Click "Publish release"

3. **Wait for Build**:
   - GitHub Actions automatically builds the Windows installer
   - MSI and ZIP files are uploaded to the release assets

### Manual Workflow Trigger

You can also manually trigger the build:
1. Go to: https://github.com/karlkauc/OeKBVisualClient/actions
2. Select "Build Windows Installer"
3. Click "Run workflow"

## Development

### Local Testing
```bash
# Run application locally
./gradlew run

# Test installer without creating release
./gradlew jpackage
```

### Cross-Platform Notes

- **Windows**: Full jpackage support (MSI/EXE)
- **macOS**: Can build, but installers require code signing
- **Linux**: Can build runtime image, but installers need platform-specific tools

The Gradle build automatically skips installer creation on non-Windows platforms during development.
