plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.formdev:flatlaf:3.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Lancer l'application avec FlatLaf sans warning"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.ubo.tp.message.MessageAppLauncher")
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}