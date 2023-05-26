plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlin.spring.get().pluginId)
}

dependencies {
    api(libs.krypto)
    api(libs.spring.boot.starter)

    api(libs.bundles.logging)
    api(libs.bundles.kotlinx.coroutines)
}
