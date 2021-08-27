import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

java.sourceCompatibility = JavaVersion.VERSION_11

val kotlinVersion = "1.5.30"
val springBootVersion = "2.3.8.RELEASE"
val kotlinJacksonVersion = "2.12.4"
val commonsVersion = "2.2021.07.01_07.04-4e699536ceb1"

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot") version "2.3.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen")
}

val githubUser: String by project
val githubPassword: String by project

repositories {
    mavenCentral()

    maven {
        url = uri("https://maven.pkg.github.com/navikt/maven-release")
    }

    maven {
        url = uri("https://maven.pkg.github.com/navikt/syfosm-common")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }

    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinJacksonVersion")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Common java modules
    implementation("no.nav.common:abac:$commonsVersion")
    implementation("no.nav.common:metrics:$commonsVersion")
    implementation("no.nav.common:job:$commonsVersion")
    implementation("no.nav.common:feature-toggle:$commonsVersion")
    implementation("no.nav.common:sts:$commonsVersion")
    implementation("no.nav.common:auth:$commonsVersion")

    implementation("no.nav.arbeid:veilarbregistrering-skjema:1.202010190947-866fe12")
    implementation("no.bekk.bekkopen:nocommons:0.11.0")
    implementation("io.confluent:kafka-avro-serializer:5.0.2")
    implementation("io.vavr:vavr:0.10.3")
    implementation("org.springdoc:springdoc-openapi-ui:1.5.9")
    implementation("net.minidev:json-smart:2.4.7")
    implementation("com.github.ben-manes.caffeine:caffeine:2.9.2")
    implementation("org.assertj:assertj-core:3.16.1")
    implementation("org.apache.kafka:kafka-clients:2.5.1")
    implementation("org.flywaydb:flyway-core:4.0.3")
    implementation("com.zaxxer:HikariCP:2.7.9")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.21")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.4")
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.1")
    runtimeOnly("com.oracle.ojdbc:ojdbc8:19.3.0.0")
    runtimeOnly("javax.inject:javax.inject:1")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.4.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("com.approvaltests:approvaltests:11.7.0")
    testImplementation("org.mock-server:mockserver-netty:5.11.2")
    testImplementation("org.mock-server:mockserver-core:5.11.2")
    testImplementation("org.mock-server:mockserver-client-java:5.11.2")
    testImplementation("org.mock-server:mockserver-junit-jupiter:5.11.2")
    testImplementation("org.glassfish.jersey.core:jersey-common:2.22.2")
    testImplementation("org.springframework:spring-test:5.2.8.RELEASE")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("org.springframework.boot:spring-boot-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:$springBootVersion")
    testImplementation("com.jayway.jsonpath:json-path-assert:2.4.0")
    testImplementation("no.nav.common:test:$commonsVersion")
}

/*publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}*/

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("app.jar")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
        if (System.getenv("CI") == "true") {
            kotlinOptions.allWarningsAsErrors = true
        }
    }
}
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("STANDARD_OUT", "STARTED", "PASSED", "FAILED", "SKIPPED")
    }
    failFast = false
}
