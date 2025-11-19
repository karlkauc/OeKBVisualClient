import java.util.Date

plugins {
    java
    application
    idea
    id("com.github.ben-manes.versions") version "0.53.0"
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

configurations {
    implementation {
        exclude(module = "stax")
        exclude(module = "stax-api")
        exclude(module = "xpp3")
    }
}

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
}

tasks.test {
    useJUnitPlatform()
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

tasks.register<Copy>("copyFiles") {
    doLast {
        copy {
            from(zipTree("jre.zip"))
            into(layout.buildDirectory.dir(project.name))
        }
        copy {
            from("LICENSE", "README.md")
            into(layout.buildDirectory.dir(project.name))
        }
        copy {
            val resourceDir = sourceSets.main.get().resources.srcDirs.first()
            from("$resourceDir${File.separator}isinlei.csv", "$resourceDir${File.separator}log4j2.properties")
            into(layout.buildDirectory.dir("${project.name}/resources"))
        }

        layout.buildDirectory.dir("${project.name}/logs").get().asFile.mkdirs()
    }
}

/* ============================================
   NATIVE JLINK & JPACKAGE TASKS
   ============================================ */

val appName = "OeKBVisualClient"
val appVersion = project.version.toString()
val mainClassName = application.mainClass.get()

// Prepare dependencies
tasks.register<Copy>("prepareDependencies") {
    group = "distribution"
    description = "Copies all dependencies to build/jars"

    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("jars"))

    dependsOn("jar")

    doLast {
        copy {
            from(tasks.jar.get().archiveFile)
            into(layout.buildDirectory.dir("jars"))
        }
    }
}

// Native jlink task
tasks.register<Exec>("jlink") {
    group = "distribution"
    description = "Creates a custom runtime image using jlink"
    dependsOn("prepareDependencies")

    val outputDir = layout.buildDirectory.dir("image").get().asFile
    val jarsDir = layout.buildDirectory.dir("jars").get().asFile

    doFirst {
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val modulePath = jarsDir.absolutePath
        val javaHome = System.getProperty("java.home")

        commandLine(
            "$javaHome/bin/jlink",
            "--module-path", "$javaHome/jmods${File.pathSeparator}$modulePath",
            "--add-modules", "java.base,java.desktop,java.logging,java.sql,java.xml,jdk.unsupported",
            "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base",
            "--add-modules", "java.management,java.naming,java.prefs",
            "--strip-debug",
            "--no-header-files",
            "--no-man-pages",
            "--compress=2",
            "--output", outputDir.absolutePath
        )
    }
}

// Native jpackage task - App Image only
tasks.register<Exec>("jpackageImage") {
    group = "distribution"
    description = "Creates an application image (no installer) using jpackage"
    dependsOn("prepareDependencies")

    val outputDir = layout.buildDirectory.dir("jpackage").get().asFile
    val jarsDir = layout.buildDirectory.dir("jars").get().asFile
    val resourceDir = file("src/main/resources")

    doFirst {
        outputDir.mkdirs()

        val javaHome = System.getProperty("java.home")
        val classpath = fileTree(jarsDir).files.joinToString(File.pathSeparator) { it.absolutePath }

        val args = mutableListOf(
            "$javaHome/bin/jpackage",
            "--type", "app-image",
            "--name", appName,
            "--app-version", appVersion,
            "--vendor", "Karl Kauc",
            "--copyright", "Copyright © 2024 Karl Kauc",
            "--description", "OeKB Visual Client",
            "--input", jarsDir.absolutePath,
            "--main-jar", tasks.jar.get().archiveFileName.get(),
            "--main-class", mainClassName,
            "--java-options", "--enable-native-access=javafx.graphics",
            "--dest", outputDir.absolutePath,
            "--resource-dir", resourceDir.absolutePath
        )

        // Add icon if available
        val iconFile = file("src/main/resources/img/connectdevelop.ico")
        if (iconFile.exists()) {
            args.addAll(listOf("--icon", iconFile.absolutePath))
        }

        commandLine(args)
    }
}

// Native jpackage task - MSI Installer
tasks.register<Exec>("jpackage") {
    group = "distribution"
    description = "Creates a Windows MSI installer using jpackage"
    dependsOn("prepareDependencies")

    val outputDir = layout.buildDirectory.dir("jpackage").get().asFile
    val jarsDir = layout.buildDirectory.dir("jars").get().asFile
    val resourceDir = file("src/main/resources")
    val licenseFile = file("LICENSE")

    doFirst {
        outputDir.mkdirs()

        val javaHome = System.getProperty("java.home")

        val args = mutableListOf(
            "$javaHome/bin/jpackage",
            "--type", "msi",
            "--name", appName,
            "--app-version", appVersion,
            "--vendor", "Karl Kauc",
            "--copyright", "Copyright © 2024 Karl Kauc",
            "--description", "OeKB Visual Client for Financial Data Platform",
            "--input", jarsDir.absolutePath,
            "--main-jar", tasks.jar.get().archiveFileName.get(),
            "--main-class", mainClassName,
            "--java-options", "--enable-native-access=javafx.graphics",
            "--dest", outputDir.absolutePath,
            "--resource-dir", resourceDir.absolutePath,
            "--win-per-user-install",
            "--win-dir-chooser",
            "--win-menu",
            "--win-shortcut",
            "--win-shortcut-prompt"
        )

        // Add license if available
        if (licenseFile.exists()) {
            args.addAll(listOf("--license-file", licenseFile.absolutePath))
        }

        // Add icon if available
        val iconFile = file("src/main/resources/img/connectdevelop.ico")
        if (iconFile.exists()) {
            args.addAll(listOf("--icon", iconFile.absolutePath))
        }

        commandLine(args)
    }
}

// Task to create portable ZIP from app image
tasks.register<Zip>("createPortableZip") {
    group = "distribution"
    description = "Creates a portable ZIP package of the application"
    dependsOn("jpackageImage")

    from(layout.buildDirectory.dir("jpackage/$appName"))
    archiveFileName.set("$appName-$appVersion-windows-x64.zip")
    destinationDirectory.set(layout.buildDirectory.dir("jpackage"))

    doFirst {
        println("Creating portable ZIP package...")
    }
}

// Task to create runtime image
tasks.register("createRuntimeImage") {
    group = "distribution"
    description = "Creates a custom runtime image using jlink"
    dependsOn("jlink")
}

// Task to create Windows installer
tasks.register("createWindowsInstaller") {
    group = "distribution"
    description = "Creates a Windows MSI installer using jpackage"
    dependsOn("jpackage")
}

// Task to create app image
tasks.register("createAppImage") {
    group = "distribution"
    description = "Creates a portable application image (no installer)"
    dependsOn("jpackageImage")
}

// Task to build all distribution packages
tasks.register("buildDistribution") {
    group = "distribution"
    description = "Builds complete distribution with runtime image, installer, and portable ZIP"
    dependsOn("jpackage", "createPortableZip")
}
