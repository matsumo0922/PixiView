plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox.feature.creator"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":core:logs"))

    implementation(libs.bundles.ui.implementation)
    implementation(libs.androidx.palette)
    implementation(libs.collapsing.toolbar.compose)
    implementation(libs.zoomable)
    implementation(libs.pinch.zoom.grid)
}
