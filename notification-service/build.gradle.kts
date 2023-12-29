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
//    spring
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
//    test
    implementation("org.springframework.kafka:spring-kafka:3.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
//    eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
//	micrometer
    implementation("io.micrometer:micrometer-observation:1.11.3")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
//	zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")
// jpa postgresql
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}
