import java.util.Date

plugins {
    // id("edu.sc.seis.launch4j") version "2.4.4"  // Commented out for now
    // id("org.openjfx.javafxplugin") version "0.1.0"  // Commented out for now
    java
    application
    idea
}

application {
    mainClass.set("StartApp")
}

version = "0.3"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

// javafx {
//     version = "25"
//     modules("javafx.controls", "javafx.fxml", "javafx.graphics")
// }

repositories {
    mavenCentral()
}

val poiVersion = "5.3.0"
val log4jVersion = "2.24.3"

configurations {
    implementation {
        exclude(module = "stax")
        exclude(module = "stax-api")
        exclude(module = "xpp3")
    }
}

dependencies {
    // JavaFX (manually added since plugin is commented)
    implementation("org.openjfx:javafx-controls:25")
    implementation("org.openjfx:javafx-fxml:25")
    implementation("org.openjfx:javafx-graphics:25")

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

    // JAXB for Java 17+
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")

    // Apache Commons Codec for Base64
    implementation("commons-codec:commons-codec:1.17.1")

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

// launch4j {
//     dontWrapJar = true
//     mainClassName = "StartApp"
//     headerType = "gui"
//     icon = "${projectDir}/img/icons8-connectdevelop.ico"
//     copyright = System.getProperty("user.name")
//     jvmOptions = listOf("-Dlog4j.configurationFile=resources/log4j2.properties")
//     bundledJre64Bit = true
//     bundledJrePath = "jre"
//     outputDir = project.name
// }

/*
tasks.register("sourceSetProperties") {
    doLast {
        sourceSets {
            main {
                println("java.srcDirs = ${java.srcDirs}")
                println("resources.srcDirs = ${resources.srcDirs}")
                println("java.files = ${java.files.map { it.name }}")
                println("allJava.files = ${allJava.files.map { it.name }}")
                println("resources.files = ${resources.files.map { it.name }}")
                println("allSource.files = ${allSource.files.map { it.name }}")
                println("output.classesDir = ${output.classesDirs}")
                println("output.resourcesDir = ${output.resourcesDir}")
                println("output.files = ${output.files}")
            }
        }
    }
}
*/

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

// tasks.named("createExe").configure {
//     dependsOn(tasks.named("copyFiles"))
// }

// tasks.register<Zip>("zipWinExe") {
//     from(layout.buildDirectory)
//     include("${project.name}/**")
//     exclude("*launch4j*")
//     archiveFileName.set("${project.name}-${version}.zip")
//     destinationDirectory.set(layout.buildDirectory)
// }

// tasks.register("doAll") {
//     dependsOn(tasks.clean, tasks.named("createExe"), tasks.named("copyFiles"), tasks.named("zipWinExe"))
// }
