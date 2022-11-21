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

dependencies{
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
