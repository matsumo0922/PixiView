plugins {
    id("pixiview.library")
    id("pixiview.detekt")
    id("pixiview.hilt")
}

android {
    namespace = "caios.android.pixiview.core.common"
}

dependencies {
    api(libs.bundles.infra.api)
    implementation(libs.libraries.core)
}
