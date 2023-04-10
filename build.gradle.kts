
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.13.1"
    id("org.jetbrains.kotlin.kapt") version "1.8.20"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    val moshi_version = "1.8.0"
    val retrofit_version = "2.9.0"
    val retrofit__logging_version = "4.9.3"
    val okhttp_version = "4.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:logging-interceptor:$retrofit__logging_version")
    implementation("com.squareup.moshi:moshi:$moshi_version")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi_version")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshi_version")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.2.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf())
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
