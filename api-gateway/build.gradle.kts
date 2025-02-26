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
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.4")
//	api gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.0.8")
//    eureka
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.0.3")
    //	keycloak
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.1.4")
//	security
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.4")
    //	micrometer
    implementation("io.micrometer:micrometer-observation:1.11.3")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.1.4")
//	zipkin
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.4")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootBuildImage {
    builder.set("paketobuildpacks/builder-jammy-base:latest")
}
