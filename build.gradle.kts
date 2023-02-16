group = "fade"
version = "0.0.1-alpha.0"
description = "inject"

if (System.getenv().containsKey("CI_GITHUB")) {
    val branchName = System.getenv("CI_GITHUB_BRANCH")
    if (branchName == "develop") version = "${(version as String)}+$branchName" // this is a bit stupid but whatever
}

plugins {
    id("java-library")
    id("maven-publish")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        name = "github-mirror"
        url = uri("https://maven.pkg.github.com/fadeoffical/mirror")
    }
}

dependencies {
    implementation("fade:mirror:0.0.2-beta.0")

    api("org.jetbrains:annotations:24.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

java {
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/fadeoffical/mirror")
            credentials {
                username = System.getenv("CI_GITHUB_USERNAME")
                password = System.getenv("CI_GITHUB_PASSWORD")
            }
        }
    }
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
