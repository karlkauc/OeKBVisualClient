import java.util.Date

plugins {
    java
    application
    idea
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.beryx.runtime") version "2.0.1"
}

application {
    mainClass.set("StartApp")
    applicationDefaultJvmArgs = listOf(
        "--enable-native-access=javafx.graphics"
    )
}

version = "0.4"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val poiVersion = "5.5.0"
val log4jVersion = "2.24.3"

dependencies {
    // Logging
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
    implementation("org.slf4j:slf4j-api:2.0.16")

    // Apache POI for Excel
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")

    // HTTP Client
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.1")
    implementation("org.apache.httpcomponents.core5:httpcore5:5.3.1")

    // Apache Commons Codec for Base64
    implementation("commons-codec:commons-codec:1.20.0")

    // Icons
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-bootstrapicons-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-win10-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-feather-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-coreui-pack:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-fontawesome-pack:12.4.0")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("net.bytebuddy:byte-buddy:1.15.10")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dnet.bytebuddy.experimental=true")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to version,
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date(),
            "Built-JDK" to System.getProperty("java.version")
        )
    }
    exclude("**/*.txt")
    exclude("**/*.xlsx")
    exclude("sass")
    exclude("**/isinlei.csv")
}

/* ============================================
   NATIVE DISTRIBUTION (org.beryx.runtime)
   ============================================ */

val appName = "OeKBVisualClient"

runtime {
    options.set(listOf(
        "--strip-debug", "--no-header-files", "--no-man-pages",
        "--compress", "zip-9"
    ))

    modules.set(listOf(
        "java.base", "java.desktop", "java.logging", "java.sql",
        "java.xml", "java.management", "java.naming", "java.prefs",
        "jdk.unsupported",
        "javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base"
    ))

    jpackage {
        imageName = appName
        installerName = appName
        appVersion = project.version.toString()
        installerType = "msi"

        jvmArgs = listOf(
            "--enable-native-access=javafx.graphics",
            "-XX:+UseG1GC",
            "-XX:+UseStringDeduplication",
            "-Xms64m",
            "-Xmx512m"
        )

        imageOptions = mutableListOf(
            "--vendor", "Karl Kauc",
            "--copyright", "Copyright © 2025 Karl Kauc",
            "--description", "OeKB Visual Client"
        ).also { opts ->
            val iconFile = file("img/icons8-connectdevelop.ico")
            if (iconFile.exists()) {
                opts.addAll(listOf("--icon", iconFile.absolutePath))
            }
        }

        installerOptions = mutableListOf(
            "--vendor", "Karl Kauc",
            "--copyright", "Copyright © 2025 Karl Kauc",
            "--description", "OeKB Visual Client for Financial Data Platform",
            "--win-per-user-install",
            "--win-dir-chooser",
            "--win-menu",
            "--win-shortcut",
            "--win-shortcut-prompt"
        ).also { opts ->
            val licenseFile = file("LICENSE")
            if (licenseFile.exists()) {
                opts.addAll(listOf("--license-file", licenseFile.absolutePath))
            }
            val iconFile = file("img/icons8-connectdevelop.ico")
            if (iconFile.exists()) {
                opts.addAll(listOf("--icon", iconFile.absolutePath))
            }
        }
    }
}

// Portable ZIP from app-image
tasks.register<Zip>("createPortableZip") {
    group = "distribution"
    description = "Creates a portable ZIP package of the application"
    dependsOn("jpackageImage")

    from(layout.buildDirectory.dir("jpackage/$appName"))
    archiveFileName.set("$appName-${project.version}-windows-x64.zip")
    destinationDirectory.set(layout.buildDirectory.dir("jpackage"))
}

// Alias tasks for backwards compatibility
tasks.register("createRuntimeImage") {
    group = "distribution"
    description = "Creates a custom runtime image using jlink"
    dependsOn("runtime")
}

tasks.register("createWindowsInstaller") {
    group = "distribution"
    description = "Creates a Windows MSI installer using jpackage"
    dependsOn("jpackage")
}

tasks.register("createAppImage") {
    group = "distribution"
    description = "Creates a portable application image (no installer)"
    dependsOn("jpackageImage")
}

tasks.register("buildDistribution") {
    group = "distribution"
    description = "Builds complete distribution with installer and portable ZIP"
    dependsOn("jpackage", "createPortableZip")
}
