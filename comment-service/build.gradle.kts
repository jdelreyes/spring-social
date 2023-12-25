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
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
// test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.testcontainers:testcontainers-bom:1.18.1")
// lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
// jpa postgresql
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
//    eureka
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
    //    resilience4j - fault taulerance interservice communication
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j:3.0.3")
    //    webflux - interservice communication
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
    //	micrometer
    implementation("io.micrometer:micrometer-observation:1.11.4")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
//	zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
