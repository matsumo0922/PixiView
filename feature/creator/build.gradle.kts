plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
}

android {
    namespace = "caios.android.pixiview.feature.creator"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))

    implementation(libs.bundles.ui.implementation)
    kapt(libs.bundles.ui.kapt)
}
