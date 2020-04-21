project.group = "io.conceptive.quarkus"
project.version = "1.0.0"

plugins {
    id("java")
    id("org.jetbrains.intellij") version "0.4.18"
}

repositories {
    mavenCentral()
    maven { setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service") }
}

intellij {
    pluginName = "QuarkusIntegration"
    version = "2020.1"
    updateSinceUntilBuild = false
    setPlugins("maven", "java")
}

dependencies {
    implementation("com.google.inject", "guice", "4.2.3")
    implementation("com.google.inject.extensions", "guice-assistedinject", "4.2.3")
}