plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.postgresql)
    api(libs.bundles.spring.web)
    api(libs.bundles.jjwt)
    api(libs.bundles.logging)
    api(libs.bundles.spring.swagger)
    api(projects.base)
}
