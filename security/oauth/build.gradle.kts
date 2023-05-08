plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.bundles.spring.web)
    api(libs.bundles.spring.jpa)
    api(projects.security.user)
    api(projects.yandex.yandexOauth)
}
