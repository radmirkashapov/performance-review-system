plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.spring.boot.starter)
    api(libs.hazelcast)
    api(libs.bundles.logging)
}
