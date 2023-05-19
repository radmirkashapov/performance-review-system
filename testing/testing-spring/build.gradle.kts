plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.bundles.spring.web)
    api(projects.security.user)
    api(projects.testing.testingApi)
}
