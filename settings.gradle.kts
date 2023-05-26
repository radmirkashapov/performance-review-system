import org.gradle.api.internal.FeaturePreviews.Feature.TYPESAFE_PROJECT_ACCESSORS

enableFeaturePreview(TYPESAFE_PROJECT_ACCESSORS.name)

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    ":base",
    ":base:caching"
)
include(
    ":yandex:yandex-oauth"
)
include(
    ":open-ai"
)
include(
    ":security:core",
    ":security:oauth",
    ":security:auth",
    ":security:user"
)
include(
    ":testing:testing-api",
    ":testing:testing-spring"
)
include(
    ":service"
)
rootProject.name = "prs"

