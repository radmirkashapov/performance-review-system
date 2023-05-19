import org.springframework.boot.gradle.dsl.SpringBootExtension

// Fix https://youtrack.jetbrains.com/issue/KTIJ-19370/Good-code-red-in-buildSrc-build.gradle.kts-when-using-libs-from-the-new-VersionCatalog-support#focus=Comments-27-6906020.0-0
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)

    id(libs.plugins.spring.boot.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
    id(libs.plugins.kotlin.jpa.get().pluginId)
}

kotlin {
    sourceSets["main"].kotlin.srcDir(rootDir.resolve("src/main/kotlin"))
}
sourceSets["main"].resources.srcDir(rootDir.resolve("src/main/resources"))
sourceSets["test"].resources.srcDir(rootDir.resolve("src/test/resources"))

dependencies {
    api(libs.commons.csv)

    api(libs.bundles.spring.jpa)
    api(libs.bundles.spring.web)

    api(projects.security.auth)
    api(projects.testing.testingSpring)

    testImplementation(libs.bundles.test.base)
    testImplementation(libs.bundles.spring.test)
}

configure<SpringBootExtension> {
    buildInfo()
}

tasks {
    bootJar {
        archiveClassifier.set("all")
        archiveVersion.set("")
    }
}
