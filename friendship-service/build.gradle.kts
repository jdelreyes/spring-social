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
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    db
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
//    lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
//    test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.testcontainers:testcontainers-bom:1.18.1")
    testImplementation("org.testcontainers:mongodb:1.18.1")
    testImplementation("org.springframework.kafka:spring-kafka-test:3.1.0")
//    webflux - interservice communication
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
//    eureka client for discovery
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
    //    resilience4j - fault taulerance interservice communication
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j:3.0.3")
    //	micrometer
    implementation("io.micrometer:micrometer-observation:1.11.4")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
//	zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")
//    kafka
    implementation("org.springframework.kafka:spring-kafka:3.1.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
