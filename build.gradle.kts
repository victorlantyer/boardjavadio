plugins {
    id("java")
    id("application")
}

group = "br.com.dio"
version = "1.0-SNAPSHOT"

layout.buildDirectory.set(layout.projectDirectory.dir("out"))

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("br.com.dio.Main")
}

dependencies {
    // Liquibase para migrations
    implementation("org.liquibase:liquibase-core:4.29.1")

    // Banco local embarcado para rodar sem infraestrutura externa
    runtimeOnly("com.h2database:h2:2.3.232")

    // MySQL Connector
    implementation("mysql:mysql-connector-java:8.0.33")
    
    // Lombok para reduzir boilerplate
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
