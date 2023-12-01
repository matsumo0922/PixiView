plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.detekt")
    id("pixiview.firebase")
}

android {
    namespace = "caios.android.fanbox.core.model"
}

dependencies {
    implementation(project(":core:common"))
}
