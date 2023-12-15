plugins {
    id("pixiview.library")
    id("pixiview.detekt")
    id("pixiview.hilt")
    id("pixiview.firebase")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "caios.android.fanbox.core.logs"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.puree)
}
