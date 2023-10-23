plugins {
    id("pixiview.library")
    id("pixiview.library.compose")
    id("pixiview.detekt")
}

android {
    namespace = "caios.android.pixiview.core.model"
}

dependencies {
    implementation(project(":core:common"))
}
