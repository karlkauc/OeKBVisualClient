# Build Instructions

## Native Windows Installer

This project uses jlink and jpackage to create native Windows installers with a bundled Java runtime.

### Prerequisites

- **JDK 17 or higher** with jpackage support (included in OpenJDK 17+)
- **WiX Toolset** (for MSI installer on Windows): https://wixtoolset.org/
- **Gradle 8.11.1+** (included via wrapper)

### Building on Windows

#### 1. Build the Application
```bash
./gradlew build
```

#### 2. Create Runtime Image (jlink)
```bash
./gradlew jlink
```
This creates a custom JRE in `build/image/` with only required modules.

#### 3. Create Windows Installer (jpackage)
```bash
./gradlew jpackage
```
This creates a Windows MSI installer in `build/jpackage/`.

#### 4. Build Everything (One Command)
```bash
./gradlew buildDistribution
```

### Installer Features

The generated Windows installer:
- ✅ **No Admin Rights Required** - Uses per-user installation (`--win-per-user-install`)
- ✅ **Custom Install Directory** - User can choose installation location
- ✅ **Start Menu Integration** - Adds application to Windows Start Menu
- ✅ **Desktop Shortcut** - Optional desktop shortcut
- ✅ **Bundled JRE** - No separate Java installation needed
- ✅ **Auto-Update Ready** - Version management built-in

### Available Gradle Tasks

| Task | Description |
|------|-------------|
| `./gradlew jlink` | Creates custom runtime image with jlink |
| `./gradlew jpackage` | Creates Windows installer with jpackage |
| `./gradlew createRuntimeImage` | Alias for jlink |
| `./gradlew createWindowsInstaller` | Alias for jpackage |
| `./gradlew buildDistribution` | Builds everything (runtime + installer) |

### Customization

Edit `build.gradle.kts` to customize:
- **App Version**: Change `version = "0.4"`
- **Vendor**: Change `vendor = "Karl Kauc"`
- **Installer Type**: Change `installerType = "msi"` to `"exe"`
- **Icon**: Place `.ico` file at `src/main/resources/img/connectdevelop.ico`

### Output Files

After successful build:
- **Runtime Image**: `build/image/OeKBVisualClient/`
- **Launcher**: `build/image/OeKBVisualClient/bin/OeKBVisualClient.bat`
- **MSI Installer**: `build/jpackage/OeKBVisualClient-0.4.msi`

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
   - MSI file is uploaded to the release assets
   - Build takes ~10-15 minutes

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
