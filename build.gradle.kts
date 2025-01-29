/**
 * bei gradle update:
 * gehe zu: ./gradle/wrapper/gradle-wrapper.properties
 * ändere distributionUrl=https\://services.gradle.org/distributions/gradle-{neue version}-bin.zip
 * anschließend: ./gradlew wrapper --gradle-version {neue version}
 */

//  Aufrufe
//  1) Microservice uebersetzen und starten
//        .\gradlew bootRun [--args='--debug']
//        .\gradlew compileJava
//        .\gradlew compileTestJava
//
//  2) Microservice als selbstausfuehrendes JAR oder Docker-Image erstellen und ausfuehren
//        .\gradlew bootJar
//        java -jar build/libs/....jar
//        .\gradlew bootBuildImage [-Pbuildpack=azul-zulu|-Pbuildpack=bellsoft]
//
//  3) Tests und Codeanalyse
//        .\gradlew test jacocoTestReport [-Dtest=rest-get] [--rerun-tasks]
//        .\gradlew jacocoTestCoverageVerification
//        .\gradlew checkstyleMain checkstyleTest spotbugsMain spotbugsTest spotlessApply modernizer
//
//  4) Sicherheitsueberpruefung durch OWASP Dependency Check und Snyk
//        .\gradlew dependencyCheckAnalyze --info
//        .\gradlew snyk-test
//
//  5) "Dependencies Updates"
//        .\gradlew versions
//        .\gradlew dependencyUpdates
//        .\gradlew checkNewVersions
//
//  6) API-Dokumentation erstellen
//        .\gradlew javadoc
//
//  7) Projekthandbuch erstellen
//        .\gradlew asciidoctor asciidoctorPdf
//
//  8) Projektreport erstellen
//        .\gradlew projectReport
//        .\gradlew dependencyInsight --dependency jakarta.persistence-api
//        .\gradlew dependencies
//        .\gradlew dependencies --configuration runtimeClasspath
//        .\gradlew buildEnvironment
//        .\gradlew htmlDependencyReport
//
//  9) Report ueber die Lizenzen der eingesetzten Fremdsoftware
//        .\gradlew generateLicenseReport
//
//  10) Daemon stoppen
//        .\gradlew --stop
//
//  11) Verfuegbare Tasks auflisten
//        .\gradlew tasks
//
//  12) "Dependency Verification"
//        .\gradlew --write-verification-metadata pgp,sha256 --export-keys
//
//  13) Native Compilation mit Spring AOT (= Ahead Of Time) in einer Eingabeaufforderung
//        "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat"
//        .\gradlew nativeCompile
//        .\build\native\nativeCompile\kunde.exe --spring.profiles.active=dev --logging.file.name=.\build\log\application.log
//        .\build\native\nativeCompile\kunde.exe --spring.datasource.url=jdbc:h2:mem:testdb --spring.datasource.username=sa --spring.datasource.password="" --logging.file.name=.\build\log\application.log
//
//  14) Initialisierung des Gradle Wrappers in der richtigen Version
//      dazu ist ggf. eine Internetverbindung erforderlich
//        gradle wrapper --gradle-version=8.8-rc-2 --distribution-type=bin

import java.nio.file.Paths
import net.ltgt.gradle.errorprone.errorprone

val javaLanguageVersion = project.properties["javaLanguageVersion"] as String? ?: JavaVersion.VERSION_23.majorVersion
val javaVersion = project.properties["javaVersion"] ?: libs.versions.javaVersion.get()

val enablePreview = if (project.properties["enablePreview"] == false) null else "--enable-preview"
val imagePath = project.properties["imagePath"] ?: "gentlecorp"

val tracePinnedThreads = project.properties["tracePinnedThreads"] == "true" || project.properties["tracePinnedThreads"] == "TRUE"
val alternativeBuildpack = project.properties["buildpack"]

val mapStructVerbose = project.properties["mapStructVerbose"] == "true" || project.properties["mapStructVerbose"] == "TRUE"
val useTracing = project.properties["tracing"] != "false" && project.properties["tracing"] != "FALSE"
val useDevTools = project.properties["devTools"] != "false" && project.properties["devTools"] != "FALSE"
val activeProfiles = if (project.properties["https"] != "false" && project.properties["https"] != "FALSE") "dev" else "dev,http"

plugins {
	java
	jacoco
	checkstyle
	idea
	`project-report`
	id("org.springframework.boot") version libs.versions.springBootPlugin.get()
	id("io.spring.dependency-management") version libs.versions.dependencyManagement.get()
	// Aufruf: gradle versions
	id("com.github.nwillc.vplugin") version libs.versions.nwillcVPlugin.get()

	// https://github.com/ben-manes/gradle-versions-plugin
	// Aufruf: gradle dependencyUpdates
	id("com.github.ben-manes.versions") version libs.versions.benManesVersions.get()

	// https://github.com/markelliot/gradle-versions
	// Aufruf: gradle checkNewVersions
	id("com.markelliot.versions") version libs.versions.markelliotVersions.get()
	id("com.github.spotbugs") version libs.versions.spotbugsPlugin.get()

	// https://github.com/diffplug/spotless
	id("com.diffplug.spotless") version libs.versions.spotless.get()

	// https://github.com/andygoossens/gradle-modernizer-plugin
	id("com.github.andygoossens.modernizer") version libs.versions.modernizerPlugin.get()

	id("net.ltgt.errorprone") version libs.versions.errorpronePlugin.get()

	id("org.asciidoctor.jvm.convert") version libs.versions.asciidoctor.get()
	id("org.asciidoctor.jvm.pdf") version libs.versions.asciidoctor.get()
}

group = "com.gentlecorp"
version = "27.01.2025"
val imageTag = project.properties["imageTag"] ?: project.version.toString()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(libs.versions.javaVersion.get())
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

extra["snippetsDir"] = file("build/generated-snippets")

configurations.checkstyle {
	resolutionStrategy.capabilitiesResolution.withCapability("com.google.collections:google-collections") {
		select("com.google.guava:guava:0")
	}
}

dependencies {

	/**--------------------------------------------------------------------------------------------------------------------
	 * SECURITY
	 * --------------------------------------------------------------------------------------------------------------------*/
	runtimeOnly("org.bouncycastle:bcpkix-jdk18on:${libs.versions.bouncycastle.get()}") // Argon2
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	// implementation("com.c4-soft.springaddons:spring-addons-starter-oidc:${libs.versions.springAddonsStarterOidc.get()}")
	implementation("org.springframework.boot:spring-boot-starter-security")


	/**------------------------------------------------------------------------------------------------------------------------
	 * SWAGGER
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
	implementation(platform("org.springdoc:springdoc-openapi:${libs.versions.springdocOpenapi.get()}"))


	/**--------------------------------------------------------------------------------------------------------------------
	 * für MAPPER
	 * --------------------------------------------------------------------------------------------------------------------*/
	annotationProcessor("org.mapstruct:mapstruct-processor:${libs.versions.mapstruct.get()}")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${libs.versions.lombokMapstructBinding.get()}")
	implementation("org.mapstruct:mapstruct:${libs.versions.mapstruct.get()}")

	/**------------------------------------------------------------------------------------------------------------------------
	 * TEST
	 * --------------------------------------------------------------------------------------------------------------------*/
	testImplementation("org.springframework.boot:spring-boot-starter-test:${libs.versions.springBootTest.get()}")
	testImplementation("org.apache.httpcomponents.client5:httpclient5:${libs.versions.httpclient5.get()}")
	implementation("org.apache.httpcomponents.core5:httpcore5:${libs.versions.httpcore5.get()}")
//	testImplementation("org.junit.jupiter:junit-jupiter")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testImplementation("org.springframework.security:spring-security-test")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	/**----------------------------------------------------------------
	 * SPRING BOOT STARTER
	 **-------------------------------------------------------------*/
	implementation(platform("org.springframework.boot:spring-boot-starter-parent:${libs.versions.springBoot.get()}"))
	implementation("org.springframework.boot:spring-boot-starter-actuator")//bei SecurityConfig
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	/**--------------------------------------------------------------------------------------------------------------------
	 * DATENBANK
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	/**------------------------------------------------------------------------------------------------------------------------
	 * MESSANGER
	 * --------------------------------------------------------------------------------------------------------------------*/
//	implementation("org.springframework.kafka:spring-kafka")

	/**------------------------------------------------------------------------------------------------------------------------
	 * WICHTIGE EXTRAS
	 * --------------------------------------------------------------------------------------------------------------------*/
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
	implementation("org.springframework.boot:spring-boot-starter-web")
	annotationProcessor("org.hibernate:hibernate-jpamodelgen:${libs.versions.hibernateJpamodelgen.get()}")
	implementation("io.github.cdimascio:dotenv-java:${libs.versions.dotenv.get()}") // Bibliothek für .env-Datei

	/**------------------------------------------------------------------------------------------------------------------------
	 * WEITERE EXTRAS
	 * --------------------------------------------------------------------------------------------------------------------*/
	implementation("com.google.guava:guava:${libs.versions.guava.get()}") //für Splitt-operation
	developmentOnly("org.springframework.boot:spring-boot-devtools:${libs.versions.springBoot.get()}")

	errorprone("com.google.errorprone:error_prone_core:${libs.versions.errorprone.get()}")
	compileOnly("com.github.spotbugs:spotbugs-annotations:${libs.versions.spotbugs.get()}")
	testCompileOnly("com.github.spotbugs:spotbugs-annotations:${libs.versions.spotbugs.get()}")
	testImplementation("org.gaul:modernizer-maven-annotations:${libs.versions.modernizer.get()}")

	/**------------------------------------------------------------------------------------------------------------------------
	 * OBSERVABILITY
	 * --------------------------------------------------------------------------------------------------------------------*/
	// Tracing durch Micrometer und Visualisierung durch Zipkin
	if (useTracing) {
		println("")
		println("Tracing mit   Z i p k i n   aktiviert")
		println("")
		implementation("io.micrometer:micrometer-tracing-bridge-brave")
		implementation("io.zipkin.reporter2:zipkin-reporter-brave")
		runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	} else {
		println("")
		println("Tracing mit   Z i p k i n   d e a k t i v i e r t")
		println("")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport) // Nach Tests Coverage Report erstellen
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // Coverage-Report erst nach den Tests generieren
	reports {
		xml.required.set(true) // Codecov benötigt das XML-Format
		html.required.set(true)
		csv.required.set(false)
	}
}

tasks.named("bootRun", org.springframework.boot.gradle.tasks.run.BootRun::class.java) {
	if (enablePreview != null) {
		jvmArgs(enablePreview)
	}

	// "System Properties", z.B. fuer Spring Properties oder fuer logback
	// https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties
	systemProperty("spring.profiles.active", activeProfiles)
	systemProperty("logging.file.name", "./build/log/application.log")
	// $env:TEMP\tomcat-docbase.* -> src\main\webapp (urspruengl. fuer WAR)
	// Document Base = Context Root, siehe https://tomcat.apache.org/tomcat-10.1-doc/config/context.html
	// $env:TEMP\hsperfdata_<USERNAME>\<PID> Java HotSpot Performance data log: bei jedem Start der JVM neu angelegt.
	// https://support.oracle.com/knowledge/Middleware/2325910_1.html
	// https://blog.mygraphql.com/zh/notes/java/diagnostic/hsperfdata/hsperfdata
	systemProperty("server.tomcat.basedir", "build/tomcat")
	systemProperty("keycloak.client-secret", project.properties["keycloak.client-secret"]!!)
	systemProperty("keycloak.issuer", project.properties["keycloak.issuer"]!!)
	//systemProperty("app.keycloak.host", project.properties["keycloak.host"]!!)

	if (tracePinnedThreads) {
		systemProperty("tracePinnedThreads", "full")
	}
}

tasks.named<JavaCompile>("compileJava") {
	// https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html
	// https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.CompileOptions.html
	// https://dzone.com/articles/gradle-goodness-enabling-preview-features-for-java
	with(options) {
		isDeprecation = true
		with(compilerArgs) {
			if (enablePreview != null) {
				add(enablePreview)
			}

			// javac --help-lint
			add("-Xlint:all,-serial,-processing,-preview")

			// https://github.com/tbroyer/gradle-errorprone-plugin#jdk-16-support
			add("--add-opens")
			add("--add-exports")

			// https://mapstruct.org/documentation/stable/reference/html/#configuration-options
			if (mapStructVerbose) {
				add("-Amapstruct.verbose=true")
			}
			//add("-Amapstruct.unmappedTargetPolicy=ERROR")
			//add("-Amapstruct.unmappedSourcePolicy=ERROR")
		}

		// https://uber.github.io/AutoDispose/error-prone
		// https://errorprone.info/docs/flags
		// https://stackoverflow.com/questions/56975581/how-to-setup-error-prone-with-gradle-getting-various-errors
		errorprone.errorproneArgs.add("-Xep:MissingSummary:OFF")

		// ohne sourceCompatiblity und targetCompatibility:
		//release = javaLanguageVersion
	}

	// https://blog.gradle.org/incremental-compiler-avoidance#about-annotation-processors
}

tasks.named("bootBuildImage", org.springframework.boot.gradle.tasks.bundling.BootBuildImage::class.java) {
	// statt "created xx years ago": https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db
	createdDate = "now"

	// default:   imageName = "docker.io/${project.name}:${project.version}"
	imageName = "$imagePath/${project.name}:$imageTag"

	@Suppress("StringLiteralDuplication")
	environment = mapOf(
		"BP_JVM_VERSION" to javaLanguageVersion, // default: 17
		"BPL_JVM_THREAD_COUNT" to "20", // default: 250 (reactive: 50)
		"BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
		"BPE_APPEND_JAVA_TOOL_OPTIONS" to enablePreview,
	)
	imageName = imageName.get()
	println("")
	println("Buildpacks: JVM durch   B e l l s o f t   L i b e r i c a   (default)")
	println("")
}

tasks.test {
	outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.named<Test>("test") {
	useJUnitPlatform {
		includeTags = when (project.properties["test"]) {
			"all" -> setOf("integration", "unit")
			"integration" -> setOf("integration")
			"rest" -> setOf("rest")
			"rest-get" -> setOf("rest-get")
			"rest-write" -> setOf("rest-write")
			"graphql" -> setOf("graphql")
			"query" -> setOf("query")
			"mutation" -> setOf("mutation")
			"unit" -> setOf("unit")
			"service-read" -> setOf("service-read")
			"service-write" -> setOf("service-write")
			else -> setOf("integration", "unit")
		}
	}

	systemProperty("spring.profiles.active", activeProfiles)
	systemProperty("junit.platform.output.capture.stdout", true)
	systemProperty("junit.platform.output.capture.stderr", true)

	val logLevelTest = project.properties["logLevelTest"] ?: "INFO"
	// systemProperty("logging.level.com.acme", logLevelTest)
	systemProperty("logging.level.org.hibernate.SQL", logLevelTest)
	systemProperty("logging.level.org.hibernate.orm.jdbc.bind", logLevelTest)
	systemProperty("logging.level.org.flywaydb.core.internal.sqlscript.DefaultSqlScriptExecutor", logLevelTest)
	systemProperty("logging.level.org.springframework.web.reactive.function.client.ExchangeFunctions", logLevelTest)
	systemProperty("logging.level.org.springframework.web.service.invoker.PathVariableArgumentResolver", logLevelTest)
	systemProperty("logging.level.org.springframework.web.service.invoker.RequestHeaderArgumentResolver", logLevelTest)
	systemProperty("logging.level.org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor", logLevelTest)
	systemProperty("logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping", logLevelTest)

	// $env:TEMP\tomcat-docbase.* -> src\main\webapp (urspruengl. fuer WAR)
	// Document Base = Context Root, siehe https://tomcat.apache.org/tomcat-10.1-doc/config/context.html
	// $env:TEMP\hsperfdata_<USERNAME>\<PID> Java HotSpot Performance data log: bei jedem Start der JVM neu angelegt.
	// https://support.oracle.com/knowledge/Middleware/2325910_1.html
	// https://blog.mygraphql.com/zh/notes/java/diagnostic/hsperfdata/hsperfdata
	systemProperty("server.tomcat.basedir", "build/tomcat")
	systemProperty("keycloak.client-secret", project.properties["keycloak.client-secret"]!!)
	systemProperty("keycloak.issuer", project.properties["keycloak.issuer"]!!)
	//systemProperty("app.keycloak.host", project.properties["keycloak.host"]!!)

	if (enablePreview != null) {
		jvmArgs(enablePreview)
	}
	if (tracePinnedThreads) {
		systemProperty("tracePinnedThreads", "full")
	}

	if (project.properties["showTestStandardStreams"] == "true" || project.properties["showTestStandardStreams"] == "TRUE") {
		testLogging.showStandardStreams = true
	}

	extensions.configure(JacocoTaskExtension::class) {
		excludes = listOf("**/entity/*_.class", "**/dev/*.class")
	}

	// https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
	// https://www.jetbrains.com/help/idea/run-debug-configuration-junit.html
	// https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
	// debug = true

	// finalizedBy("jacocoTestReport")
}

jacoco {
	toolVersion = libs.versions.jacoco.get()
}

// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/#configuring-tasks
tasks.named<JacocoReport>("jacocoTestReport") {
	reports {
		xml.required = true
		html.required = true
	}

	classDirectories.setFrom(classDirectories.files.map {
		fileTree(it).matching {
			exclude(listOf("**/entity/*_.class", "**/dev/*.class"))
		}
	})

	// https://docs.gradle.org/current/userguide/jacoco_plugin.html
	// https://github.com/gradle/gradle/pull/12626
	dependsOn(tasks.test)
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
	violationRules {
		rule {
			limit { minimum = BigDecimal("0.7") }
		}
	}
}

checkstyle {
	toolVersion = libs.versions.checkstyle.get()
	isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		xml.required = true
		html.required = true
	}
}

spotbugs {
	// https://github.com/spotbugs/spotbugs/releases
	toolVersion = libs.versions.spotbugs.get()
}

tasks.named("spotbugsMain", com.github.spotbugs.snom.SpotBugsTask::class.java) {
	reportLevel = com.github.spotbugs.snom.Confidence.LOW
	reports.create("html") { required = true }
	// val excludePath = File("config/spotbugs/exclude.xml")
	val excludePath = Paths.get("config", "spotbugs", "exclude.xml")
	excludeFilter = file(excludePath)
}

modernizer {
	toolVersion = libs.versions.modernizer.get()
	includeTestClasses = true
}

tasks.named<Javadoc>("javadoc") {
	options {
		showFromPackage()
		// outputLevel = org.gradle.external.javadoc.JavadocOutputLevel.VERBOSE

		if (this is CoreJavadocOptions) {
			// Keine bzw. nur elementare Warnings anzeigen wegen Lombok
			// https://stackoverflow.com/questions/52205209/configure-gradle-build-to-suppress-javadoc-console-warnings
			addStringOption("Xdoclint:none", "-quiet")
			// https://stackoverflow.com/questions/59485464/javadoc-and-enable-preview
			addBooleanOption("-enable-preview", true)
			addStringOption("-release", javaLanguageVersion)
		}

		if (this is StandardJavadocDocletOptions) {
			author(true)
			bottom("Copyright &#169; 2016 - present J&uuml;rgen Zimmermann, Hochschule Karlsruhe. All rights reserved.")
		}
	}
}


tasks.named("dependencyUpdates", com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class) {
	checkConstraints = true
}

idea {
	module {
		isDownloadSources = true
		isDownloadJavadoc = true
		// https://stackoverflow.com/questions/59950657/querydsl-annotation-processor-and-gradle-plugin
		sourceDirs.add(file("generated/"))
		generatedSourceDirs.add(file("generated/"))
	}
}
