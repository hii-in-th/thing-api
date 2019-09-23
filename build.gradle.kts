/*
 * Copyright (c) 2019 NSTDA
 *   National Science and Technology Development Agency, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.flywaydb.gradle.task.FlywayCleanTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
    id("org.jmailen.kotlinter") version "1.26.0"
    id("com.moonlitdoor.git-version") version "0.1.1"
    id("org.jetbrains.dokka") version "0.9.18"
    id("org.flywaydb.flyway") version "6.0.0"
}

group = "hii"
version = gitVersion

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    //Application dependency block

    dokkaRuntime("org.jetbrains.dokka:dokka-fatjar:0.9.18")

    compile(kotlin("stdlib-jdk8"))
    compile("args4j:args4j:2.33")

    val log4jVersion = "2.11.1"
    compile("org.apache.logging.log4j:log4j-core:$log4jVersion")
    compile("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    compile("io.github.microutils:kotlin-logging:1.6.26")

    compile("redis.clients:jedis:3.0.1")
    compile("org.jetbrains.exposed:exposed:0.13.7")
    compile("org.postgresql:postgresql:42.2.5")
    compile("com.zaxxer:HikariCP:3.3.1")

    compile("com.google.code.gson:gson:2.8.5")

    val fuelVersion = "2.2.1"
    compile("com.github.kittinunf.fuel:fuel:$fuelVersion")
    compile("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")

    compile("com.auth0:java-jwt:3.8.1")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    compile("commons-codec:commons-codec:1.12")
    compile("com.fatboyindustrial.gson-javatime-serialisers:gson-javatime-serialisers:1.1.1")
    testImplementation("com.github.fppt:jedis-mock:0.1.13")
    testImplementation("ru.yandex.qatools.embed:postgresql-embedded:2.10")
    testImplementation("org.flywaydb:flyway-core:6.0.0")
}

dependencies {
    //Core framework dependency block
    val jerseyVersion = "2.28"
    compile("org.glassfish.jersey.core:jersey-common:$jerseyVersion")
    compile("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")
    compile("org.glassfish.jersey.containers:jersey-container-servlet-core:$jerseyVersion")
    compile("org.glassfish.jersey.containers:jersey-container-jetty-servlet:$jerseyVersion")
    // Remove jersey-media-json-jackson production
    compile("org.glassfish.jersey.media:jersey-media-json-jackson:$jerseyVersion")
    testImplementation("org.glassfish.jersey.test-framework:jersey-test-framework-core:$jerseyVersion")
    testImplementation("org.glassfish.jersey.test-framework.providers:jersey-test-framework-provider-grizzly2:$jerseyVersion")

    val jettyVersion = "9.4.18.v20190429"
    compile("org.eclipse.jetty:jetty-server:$jettyVersion")
    compile("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    compile("org.eclipse.jetty:jetty-http:$jettyVersion")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.28.2")
    testImplementation("org.amshove.kluent:kluent:1.53")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allJava)
}

tasks.named<Jar>("jar") {
    configurations.compileClasspath.get().forEach { if (it.isDirectory) from(it) else from(zipTree(it)) }

    this.archiveName = "${project.name}.jar"
    this.destinationDir = file("$rootDir/build/bin")

    manifest { attributes["Main-Class"] = "hii.thing.api.ThingApiServer" }

    exclude(
        "META-INF/*.RSA",
        "META-INF/*.SF",
        "META-INF/*.DSA",
        "META-INF/DEPENDENCIES",
        "META-INF/NOTICE*",
        "META-INF/LICENSE*",
        "about.html"
    )
}
tasks.dokka {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/javadoc"
}

tasks.creating(FlywayCleanTask::class) {
    url = "jdbc:postgresql://127.0.0.1:27365/postgres"
    user = "postgres"
    password = "postgres"
}
