plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox.feature.post"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))

    implementation(libs.bundles.ui.implementation)
    implementation(libs.zoomable)
}
