plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}

group = "ca.springsocial"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // spring boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

//lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

//    password hashing
    compileOnly("org.springframework.security:spring-security-core:6.1.4")
    implementation("org.springframework.security:spring-security-crypto:6.1.4")

    // sql database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")

    // webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.5")

    // eureka
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")

    // testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.testcontainers:testcontainers-bom:1.18.1")
    testImplementation("org.testcontainers:postgresql:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
