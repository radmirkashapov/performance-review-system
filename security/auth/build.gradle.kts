plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.kotlin.user.agents)
    api(libs.bundles.spring.web)
    api(projects.security.oauth)
}
