plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "17.0.2"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}
val flatlafVersion = "3.2"
val twoslicesVersion = "0.9.4"
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.formdev:flatlaf:$flatlafVersion")
    implementation("com.sshtools:two-slices:$twoslicesVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Lancer l'application Swing avec FlatLaf sans warning"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.ubo.tp.message.MessageAppLauncher")
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

tasks.register<JavaExec>("runAppFx") {
    group = "application"
    description = "Lancer l'application JavaFX"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.ubo.tp.message.MessageAppFxLauncher")
    jvmArgs = listOf(
        "--enable-native-access=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"
    )
}

tasks.register<Jar>("runAppJar") {
    group = "build"
    description = "Génère un JAR exécutable (fat jar) pour runApp"
    archiveBaseName.set("runApp")
    archiveVersion.set(version.toString())
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.exists() }.map { if (it.isDirectory) it else zipTree(it) }
    })

    manifest {
        attributes["Main-Class"] = "com.ubo.tp.message.MessageAppLauncher"
    }
}

tasks.register<Exec>("execRunAppJar") {
    group = "application"
    description = "Exécute le jar généré avec les mêmes jvmArgs que runApp"
    dependsOn("runAppJar")

    doFirst {
        val jarFile = tasks.named<Jar>("runAppJar").get().archiveFile.get().asFile.absolutePath
        commandLine = listOf("java", "--enable-native-access=ALL-UNNAMED", "-jar", jarFile)
    }
}
