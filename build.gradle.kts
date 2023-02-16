// build uses unstable features
// might've been a bad idea?
@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

plugins {
    java
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

//    maven {
//        name = "github-mirror"
//        url = uri("https://maven.pkg.github.com/fadeoffical/mirror")
//    }
}

dependencies {
    implementation("fade:mirror:0.0.2-beta.0")

    api("org.jetbrains:annotations:24.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

java {
    withJavadocJar()
    withSourcesJar()
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

group = "fade"
version = "0.0.1-alpha.0"
description = "inject"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
