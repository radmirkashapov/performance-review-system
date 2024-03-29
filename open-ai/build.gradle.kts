plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.bundles.openai.client)

    api(projects.base)
    api(projects.testing.testingApi)
}
