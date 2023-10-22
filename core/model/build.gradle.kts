plugins {
    id("kanade.library")
    id("kanade.library.compose")
    id("kanade.detekt")
}

android {
    namespace = "caios.android.pixiview.core.model"
}

dependencies {
    implementation(project(":core:common"))
        implementation(libs.androidx.media)
}
