plugins {
    id("kanade.library")
    id("kanade.detekt")
    id("kanade.hilt")
}

android {
    namespace = "caios.android.pixiview.core.common"
}

dependencies {
    api(libs.bundles.infra.api)
    implementation(libs.libraries.core)
}
