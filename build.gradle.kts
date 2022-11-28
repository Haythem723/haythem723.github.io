plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.0"
}

group = "net.diyigemt.mpu"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

val ktor_version = "2.1.3"

dependencies{
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.create("buildAndDeploy"){
    group = "mirai"
    dependsOn("buildPlugin")
    doLast {
        exec{
            workingDir("$rootDir")
            commandLine("cmd", "/c", "deploy.cmd", "${rootProject.name}-$version.mirai2.jar")
        }
    }
}
