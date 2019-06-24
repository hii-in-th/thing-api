import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
    id("org.jmailen.kotlinter") version "1.26.0"
    id("com.moonlitdoor.git-version") version "0.1.1"
}

group = "hii"
version = gitVersion

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    //Application dependency block
    compile(kotlin("stdlib-jdk8"))
    compile("args4j:args4j:2.33")

    val log4jVersion = "2.11.1"
    compile("org.apache.logging.log4j:log4j-core:$log4jVersion")
    compile("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")

    compile("redis.clients:jedis:3.0.1")
    compile("org.jetbrains.exposed:exposed:0.13.7")
    compile("org.postgresql:postgresql:42.2.5")
    compile("com.zaxxer:HikariCP:3.3.1")

    compile("com.google.code.gson:gson:2.8.5")

    compile("com.auth0:java-jwt:3.8.1")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.1.1")
    compile("commons-codec:commons-codec:1.12")

    testImplementation("com.github.fppt:jedis-mock:0.1.13")
    testImplementation("ru.yandex.qatools.embed:postgresql-embedded:2.10")
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
    testImplementation("org.amshove.kluent:kluent:1.48")
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
