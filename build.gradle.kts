plugins {
	java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.avast.gradle.docker-compose") version "0.17.5"   // <-- orchestration
}

group = "com.wizardry"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
/** tie Gradle tasks to the DB container */
dockerCompose {
    useComposeFiles.set(listOf("docker-compose.yaml"))
    isRequiredBy(tasks.bootRun)          // `./gradlew bootRun`  → starts DB first
    isRequiredBy(tasks.test)             // tests run against same container

    /**
     * ───────── Locate the Docker CLI ─────────
     *
     * Priority order:
     *   1.  Gradle property   -PdockerExecutable=/path/to/docker
     *   2.  gradle.properties -> dockerExecutable=/path/to/docker
     *   3.  Environment var   DOCKER_BIN=/path/to/docker
     *   4.  Common defaults per OS
     *   5.  Fallback to just “docker” (expects PATH to be correct)
     */
    val dockerPath: Provider<File> = providers
        .gradleProperty("dockerExecutable")
        .orElse(providers.environmentVariable("DOCKER_BIN"))
        .map { file(it) }
        .orElse(
            providers.provider {
                val os = System.getProperty("os.name").lowercase()
                when {
                    os.startsWith("windows") ->
                        File("C:/Program Files/Docker/Docker/resources/bin/docker.exe")
                    System.getenv("HOMEBREW_PREFIX") != null ->
                        File("${System.getenv("HOMEBREW_PREFIX")}/bin/docker")
                    else ->
                        File("/usr/local/bin/docker")
                }
            }
        )

    dockerExecutable.set(dockerPath.get().path)
}
