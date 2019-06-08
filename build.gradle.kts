import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
    id("org.jlleitschuh.gradle.ktlint") version "4.1.0"
    id("com.moonlitdoor.git-version") version "0.1.1"
}

group = "hii"
version = gitVersion

repositories {
    mavenCentral()
}

dependencies { //Application dependency block
    compile(kotlin("stdlib-jdk8"))
    compile("args4j:args4j:2.33")
}

dependencies { //Core framework dependency block
    val jerseyVersion = "2.28"
    compile("org.glassfish.jersey.core:jersey-common:$jerseyVersion")
    compile("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")
    compile("org.glassfish.jersey.containers:jersey-container-servlet-core:$jerseyVersion")
    compile("org.glassfish.jersey.containers:jersey-container-jetty-servlet:$jerseyVersion")
    testImplementation("org.glassfish.jersey.test-framework:jersey-test-framework-core:$jerseyVersion")
    testImplementation("org.glassfish.jersey.test-framework.providers:jersey-test-framework-provider-grizzly2:$jerseyVersion")

    val jettyVersion = "9.4.18.v20190429"
    compile("org.eclipse.jetty:jetty-server:$jettyVersion")
    compile("org.eclipse.jetty:jetty-servlet:$jettyVersion")
    compile("org.eclipse.jetty:jetty-http:$jettyVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Jar>("jar") {
    configurations.compile.forEach { if (it.isDirectory) from(it) else from(zipTree(it)) }

    archiveName = "${project.name}.jar"
    destinationDir = file("$rootDir/build/bin")

    manifest { attributes.put("Main-Class", "hii.thing.api.ThingApiServer") }

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
