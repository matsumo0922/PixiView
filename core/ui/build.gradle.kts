plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.hilt")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox.core.ui"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:repository"))
    implementation(project(":core:datastore"))
    implementation(project(":core:logs"))

    implementation(libs.bundles.ui.implementation)

    implementation(libs.androidx.biomtrics)
    implementation(libs.androidx.biomtrics.ktx)
    implementation(libs.androidx.palette)
    implementation(libs.play.service.ads)
    implementation(libs.collapsing.toolbar.compose)
    implementation(libs.reorderble.compose)
}
