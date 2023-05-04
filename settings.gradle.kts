import org.gradle.api.internal.FeaturePreviews.Feature.TYPESAFE_PROJECT_ACCESSORS

enableFeaturePreview(TYPESAFE_PROJECT_ACCESSORS.name)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    
}

include(
    ":yandex:yandex-oauth"
)
include(
    ":service"
)
rootProject.name = "prs"

