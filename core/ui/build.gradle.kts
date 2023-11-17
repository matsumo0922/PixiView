plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
}

android {
    namespace = "caios.android.pixiview.core.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))

    implementation(libs.bundles.ui.implementation)

    implementation(libs.androidx.palette)
    implementation(libs.collapsing.toolbar.compose)
    implementation(libs.reorderble.compose)
}
