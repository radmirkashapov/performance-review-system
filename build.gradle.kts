@file:Suppress("UnstableApiUsage")

import io.gitlab.arturbosch.detekt.Detekt
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

buildscript {
    dependencies {
        classpath(libs.gradle.kotlinx.atomicfu)
    }
}

// Fix https://youtrack.jetbrains.com/issue/KTIJ-19370/Good-code-red-in-buildSrc-build.gradle.kts-when-using-libs-from-the-new-VersionCatalog-support#focus=Comments-27-6906020.0-0
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.deps) apply false

    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kover)
}

subprojects {
    project.pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm.get().pluginId) {
        plugins.apply(rootProject.libs.plugins.kotlin.jpa.get().pluginId)
        plugins.apply(rootProject.libs.plugins.spring.deps.get().pluginId)
        plugins.apply(rootProject.libs.plugins.detekt.get().pluginId)
        plugins.apply(rootProject.libs.plugins.kover.get().pluginId)

        repositories {
            mavenCentral()
        }

        apply<DependencyManagementPlugin>()
        configure<DependencyManagementExtension> {
            imports { mavenBom(SpringBootPlugin.BOM_COORDINATES) }
        }
        configurations.all {
            resolutionStrategy {
                failOnVersionConflict()
                failOnDynamicVersions()
                failOnNonReproducibleResolution()

                eachDependency {
                    @Suppress("SpellCheckingInspection")
                    when (requested.group) {
                        "io.mockk" -> useVersion(rootProject.libs.versions.mockk.get())
                        "io.kotest" -> useVersion(rootProject.libs.versions.kotest.lib.get())
                        "org.objenesis" -> useVersion(rootProject.libs.versions.objenesis.get())
                        "io.github.classgraph" -> useVersion(rootProject.libs.versions.classgraph.get())
                        "net.java.dev.jna" -> useVersion(rootProject.libs.versions.jna.get())
                        "org.apache.logging.log4j" -> useVersion(rootProject.libs.versions.log4j.get())
                        "org.testcontainers" -> useVersion(rootProject.libs.versions.testcontainers.get())
                        "org.jetbrains" -> when (requested.name) {
                            "annotations" -> useVersion(rootProject.libs.versions.jetbrains.annotations.get())
                        }

                        "org.jetbrains.kotlinx" -> when (requested.name) {
                            "kotlinx-serialization-json" -> useVersion(rootProject.libs.versions.kotlinx.serialization.json.get())
                        }

                        "com.fasterxml.jackson" -> when (requested.name) {
                            "jackson-bom" -> useVersion(rootProject.libs.versions.jacksonBom.get())
                        }

                        "javax.xml.bind" -> when (requested.name) {
                            "jaxb-api" -> useVersion(rootProject.libs.versions.jaxb.api.get())
                        }

                        "org.apache.commons" -> when(requested.name) {
                            "commons-collections4" -> useVersion(rootProject.libs.versions.commons.collections4.get())
                        }
                    }
                }
            }
        }

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        }

        tasks.withType<KotlinCompile> {
            kotlinOptions {
                @Suppress("SpellCheckingInspection")
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-progressive",
                    "-Xskip-prerelease-check",
                    "-opt-in=kotlin.time.ExperimentalTime",
                    "-opt-in=io.kotest.common.ExperimentalKotest"
                )
                jvmTarget = libs.versions.jvm.get()
                languageVersion = "1.9"
            }
        }

        kover {
            disabledForProject = false

            useKoverTool()
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            systemProperty("SPRING_PROFILES_ACTIVE", "test")

            failFast = true
            filter {
                isFailOnNoMatchingTests = false
            }

            testLogging {
                showStandardStreams = true
                showExceptions = true
                exceptionFormat = FULL
                events(FAILED, PASSED, SKIPPED)
            }
        }
        tasks.withType<Detekt> {
            parallel = false
            buildUponDefaultConfig = true
            config.from(rootDir.resolve("detekt/detekt.yml"))
        }
    }
    project.pluginManager.withPlugin(rootProject.libs.plugins.kotlin.spring.get().pluginId) {
        plugins.apply(rootProject.libs.plugins.kotlin.kapt.get().pluginId)
        configure<org.jetbrains.kotlin.gradle.plugin.KaptExtension> {
            correctErrorTypes = true
            strictMode = true
        }
        dependencies {
            "kapt"(libs.spring.boot.configuration.processor)
//			"kapt"(libs.spring.context.indexer)
        }
    }
}
