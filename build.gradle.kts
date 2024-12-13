import org.springframework.boot.gradle.tasks.bundling.BootJar

val jooqVersion by extra("3.19.13")
val testcontainersVersion by extra("1.20.1")
val flywayVersion by extra("11.0.1")

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
    id("pmd")
    id("org.jooq.jooq-codegen-gradle") version "3.19.13"
    id("com.diffplug.spotless") version "6.25.0"
}

sourceSets {
    main {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
    test {
        java {
            srcDir("build/generated-sources/jooq")
        }
    }
}

group = "dev.wuan.wuan"
version = "1.0.0"
description = "基于最新平台的基础架构服务"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
maven { url = uri("https://maven.aliyun.com/repository/central")}
maven { url = uri("https://maven.aliyun.com/repository/public") }
maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots") }
mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.testcontainers:rabbitmq:$testcontainersVersion")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.jooq:jooq-meta:$jooqVersion")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.flywaydb:flyway-mysql:$flywayVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mysql:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    runtimeOnly("com.mysql:mysql-connector-j:8.2.0")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    jooqCodegen("com.mysql:mysql-connector-j:8.2.0")
    jooqCodegen("org.jooq:jooq-codegen:$jooqVersion")
    jooqCodegen("org.jooq:jooq-meta-extensions:$jooqVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<BootJar> {
    archiveFileName.set("baseplatfrom.jar")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

pmd {
    isConsoleOutput = true
    toolVersion = "7.8.0"
    rulesMinimumPriority.set(5)
    ruleSetFiles = files("pmd-rules.xml")
}

spotless {
    format("misc") {
        // define the files to apply `misc` to
        target("*.gradle.kts", "*.md", ".gitignore")
        // define the steps to apply to those files
        trimTrailingWhitespace()
        indentWithSpaces() // or spaces. Takes an integer argument if you don't like 4
        endWithNewline()
    }

    java {
        googleJavaFormat("1.25.2").reflowLongStrings()
        formatAnnotations()
    }

    kotlinGradle {
        target("*.gradle.kts") // default target for kotlinGradle
        ktlint() // or ktfmt() or prettier()
    }
}

jooq {
    configuration {
        generator {
            database {
                includes = ".*"
                excludes = "qrtz_.*"
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/migration/*.sql"
                    }
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                    property {
                        key = "logExecutedQueries"
                        value = "true"
                    }
                    property {
                        key = "logExecutionResults"
                        value = "true"
                    }
                }
                forcedTypes {
                    forcedType {
                        name = "varchar"
                        includeExpression = ".*"
                        includeTypes = "JSONB?"
                    }
                    forcedType {
                        name = "varchar"
                        includeExpression = ".*"
                        includeTypes = "INET"
                    }
                    forcedType {
                        name = "Boolean"
                        includeExpression = ".*"
                        includeTypes = "(?i:TINYINT\\(1\\))"
                    }
                    forcedType {
                        name = "OffsetDateTime"
                        includeExpression = ".*"
                        includeTypes = "TIMESTAMP"
                    }
                }
            }
            generate {
                isDaos = true
                isRecords = true
                isDeprecated = false
                isImmutablePojos = false
                isFluentSetters = true
                isSpringAnnotations = true
                isSpringDao = true
            }
            target {
                packageName = "org.jooq.generated"
            }
        }
    }
}
